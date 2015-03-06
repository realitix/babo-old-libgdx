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

/**
 * Textures UV contains UV for each colum of the matrix
 * Lines are in order U1, U2, V1, V2
 * AlphaMap contain 4 channels. By default, texture0 is used
 * a -> mat4[0]
 * r -> mat4[1]
 * g -> mat4[2]
 * b -> mat4[3]
 * Illumination inspiration https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson6
*/

uniform mat4 u_textureUvs;
uniform sampler2D u_diffuseAtlas;
uniform sampler2D u_normalAtlas;
uniform sampler2D u_specularAtlas;
uniform sampler2D u_alphaMap;
uniform sampler2D u_shadowTexture;
uniform vec2 u_tillSize;
uniform float u_shadowPCFOffset;
uniform DirectionalLight u_dirLights[numDirectionalLights];
uniform PointLight u_pointLights[numPointLights];
uniform SpotLight u_spotLights[numSpotLights];
uniform int u_numCurrDirectionalLights;
uniform int u_numCurrPointLights;
uniform int u_numCurrSpotLights;

varying vec2 v_diffuseUV;
varying vec2 v_alphaMapUV;
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


vec2 getBaboTexture(int n, in vec2 textureUV) {
	textureUV.x = u_textureUvs[n].x + (u_textureUvs[n].y - u_textureUvs[n].x) * textureUV.x;
	textureUV.y = u_textureUvs[n].z + (u_textureUvs[n].w - u_textureUvs[n].z) * textureUV.y;
	return textureUV;
}

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

/**
 * On commence par recuperer l'alphamap
 * Si une seule texture, facile, on continue avec celle la
 * Sinon
 * On recupere les textures normal (contenant le displacement en alpha)
 * On fait le test de mix, si une est totalement visible, on continue avec cella la
 * Sinon on mix les 2 normals, diffuse, specular
*/
void main( void )
{
	if( v_alphaMapUV.x > 1.0 || v_alphaMapUV.y > 1.0 || v_alphaMapUV.x < 0.0 || v_alphaMapUV.y < 0.0 ) {
		gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
		return;
	}
	
	// We till the texture
	vec2 finalTextureUV = fract(v_alphaMapUV.xy / u_tillSize);
	
	// Load Textures
	vec2 texture0UV = getBaboTexture(0, finalTextureUV);
	vec4 diffuseColor0 = texture2D(u_diffuseAtlas, texture0UV);
	vec4 normalColor0 = normalize(texture2D(u_normalAtlas, texture0UV).rgba * 2.0 - 1.0); 
	vec4 specularColor0 = texture2D(u_specularAtlas, texture0UV);
	
	vec2 texture1UV = getBaboTexture(1, finalTextureUV);
	vec4 diffuseColor1 = texture2D(u_diffuseAtlas, texture1UV);
	vec4 normalColor1 = normalize(texture2D(u_normalAtlas, texture1UV).rgba * 2.0 - 1.0);
	vec4 specularColor1 = texture2D(u_specularAtlas, texture1UV);
	
	// Init final parameters
	vec3 normal;
	vec4 diffuse;
	vec4 specular;
	vec3 lightSpecular = vec3(0.0);
	vec3 lightDiffuse = vec3(0.0);
	
	// We get alpha map
	vec4 alphaColor = texture2D(u_alphaMap, v_alphaMapUV);
	float alphaIntensity = (alphaColor.r + alphaColor.g + alphaColor.b) / 3.0;
	
	// Full texture
	if( alphaIntensity == 0.0 ) {
		normal = vec3(normalColor0);		
		diffuse = vec4(diffuseColor0);
		specular = vec4(specularColor0);
	}
	else if( alphaIntensity == 1.0 ) {
		normal = vec3(normalColor1);
		diffuse = vec4(diffuseColor1);
		specular = vec4(specularColor1);
	}
	// Fade texture
	else {
		// Good texture splatting algorythme
		// normalColor0.a containe height map
		float a1 = alphaIntensity;
		float a2 = 1.0 - a1;
		float depth = 0.2;
	    float ma = max(normalColor0.a + a1, normalColor1.a + a2) - depth;
	
	    float b1 = max(normalColor0.a + a1 - ma, 0.0);
	    float b2 = max(normalColor1.a + a2 - ma, 0.0);
	    
	    if( b1 > b2 ) {
	    	normal = vec3(normalColor0);
	    	specular = vec4(specularColor0);
	    }
	    else {
	    	normal = vec3(normalColor1);
	    	specular = vec4(specularColor1);
	    }
		diffuse = vec4((diffuseColor0.rgb * b1 + diffuseColor1.rgb * b2) / (b1 + b2), 1.0);
	}
	
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