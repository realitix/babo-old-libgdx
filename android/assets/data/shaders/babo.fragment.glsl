#ifdef GL_ES
precision highp float;
#endif

const float u_shininess = 10.0;
const int numPointLights = 10;
const int numSpotLights = 10;
const int numDirectionalLights = 2;

struct PointLight {
	vec3 color;
	vec3 position;
	float intensity;
};

struct SpotLight {
	vec3 color;
	vec3 position;
	vec3 direction;
	float intensity;
	float angleCos;
	float exponent;
};

struct DirectionalLight {
	vec3 color;
	vec3 direction;
};

uniform sampler2D u_diffuseAtlas;
uniform sampler2D u_normalAtlas;
uniform sampler2D u_specularAtlas;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
uniform DirectionalLight u_dirLights[numDirectionalLights];
uniform PointLight u_pointLights[numPointLights];
uniform SpotLight u_spotLights[numSpotLights];
uniform int u_numCurrDirectionalLights;
uniform int u_numCurrPointLights;
uniform int u_numCurrSpotLights;

varying vec2 v_diffuseUV;
varying vec3 v_pos;

#if (QUALITY != MIN)
	varying vec3 v_normal;
	varying vec3 v_binormal;
	varying vec3 v_tangent;
	varying vec3 v_viewVec;
#endif

#if (QUALITY == MAX)
	varying vec3 v_shadowMapUv;
#endif

#if (QUALITY == MAX)
float getShadowness(vec2 offset) {
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));
}

float getShadow() {
	return (
			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif


void main( void )
{
	// Init final parameters
	vec4 diffuse = texture2D(u_diffuseAtlas, v_diffuseUV);
	vec3 normal = normalize(texture2D(u_normalAtlas, v_diffuseUV).rgb * 2.0 - 1.0);
	vec4 specular = texture2D(u_specularAtlas, v_diffuseUV);
	vec3 lightSpecular = vec3(0.0);
	vec3 lightDiffuse = vec3(0.0);
	
	#if (QUALITY == MIN)
		gl_FragColor = vec4((diffuse.rgb), 1.0);
		return;
	#else
		
	normal = normalize((v_tangent * normal.x) + (v_binormal * normal.y) + (v_normal * normal.z));
	
	// Directional Lights
	for (int i = 0; i < u_numCurrDirectionalLights; i++) {
		vec3 lightDir = -u_dirLights[i].direction;
		
		// Diffuse
		float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);
		lightDiffuse.rgb += u_dirLights[i].color * NdotL;
		
		// Specular
		float halfDotView = dot(normal, normalize(lightDir + v_viewVec));
		lightSpecular += u_dirLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess), 0.0, 1.0);
	}
	
	// Point Lights
	for (int i = 0; i < u_numCurrPointLights; i++) {
		vec3 lightDir = u_pointLights[i].position - v_pos;
		float dist2 = dot(lightDir, lightDir);
		lightDir *= inversesqrt(dist2);
		float NdotL = clamp(dot(normal, lightDir), 0.0, 2.0);
		float falloff = clamp(u_pointLights[i].intensity / (1.0 + dist2), 0.0, 2.0); // FIXME mul intensity on cpu
		
		// Diffuse
		lightDiffuse += u_pointLights[i].color * (NdotL * falloff);
		
		// Specular
		float halfDotView = clamp(dot(normal, normalize(lightDir + v_viewVec)), 0.0, 2.0);
		lightSpecular += u_pointLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess) * falloff, 0.0, 2.0);
	}
	
	// Spot Lights
	for (int i = 0; i < u_numCurrSpotLights; i++) {
		vec3 lightDir = u_spotLights[i].position - v_pos;

		float spotEffect = dot(-normalize(lightDir), normalize(u_spotLights[i].direction));
		if ( spotEffect  > u_spotLights[i].angleCos ) {
			spotEffect = max( pow( max( spotEffect, 0.0 ), u_spotLights[i].exponent ), 0.0 );

			float dist2 = dot(lightDir, lightDir);
			lightDir *= inversesqrt(dist2);
			float NdotL = clamp(dot(normal, lightDir), 0.0, 2.0);
			float falloff = clamp(u_spotLights[i].intensity / (1.0 + dist2), 0.0, 2.0); // FIXME mul intensity on cpu
		
			// Diffuse
			lightDiffuse += u_spotLights[i].color * (NdotL * falloff) * spotEffect;
		
			// Specular
			float halfDotView = clamp(dot(normal, normalize(lightDir + v_viewVec)), 0.0, 2.0);
			lightSpecular += u_spotLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess) * falloff, 0.0, 2.0) * spotEffect;
		}
	}
	
	diffuse.rgb *= lightDiffuse;
	specular.rgb *= lightSpecular;
	
	#if (QUALITY == MAX)
		gl_FragColor = vec4((diffuse.rgb + specular.rgb)*getShadow(), 1.0);
	#else
		gl_FragColor = vec4((diffuse.rgb + specular.rgb), 1.0);
	#endif
	
	#endif // End quality not min
}