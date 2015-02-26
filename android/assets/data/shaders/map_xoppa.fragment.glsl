#ifdef GL_ES
precision highp float;
#endif

struct PointLight {
	vec3 color;
	vec3 position;
	float intensity;
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

const int numPointLights = 10;
const int numDirectionalLights = 2;

uniform mat4 u_textureUvs;
uniform sampler2D u_diffuseAtlas;
uniform sampler2D u_normalAtlas;
uniform sampler2D u_specularityAtlas;
uniform sampler2D u_alphaMap;
uniform sampler2D u_shadowTexture;
uniform vec2 u_tillSize;
uniform float u_shadowPCFOffset;
uniform DirectionalLight u_dirLights[numDirectionalLights];
uniform PointLight u_pointLights[numPointLights];
uniform int u_numCurrDirectionalLights;
uniform int u_numCurrPointLights;

varying vec2 v_diffuseUV;
varying vec3 v_shadowMapUv;
varying vec2 v_alphaMapUV;
varying vec3 v_normal;
varying vec3 v_binormal;
varying vec3 v_tangent;
varying vec3 v_lightDiffuse;
varying vec3 v_viewVec;
varying vec3 v_pos;

const float u_shininess = 20.0;


vec3 baboMix(vec4 texture1, float a1, vec4 texture2, float a2) {
    float depth = 0.2;
    float ma = max(texture1.a + a1, texture2.a + a2) - depth;

    float b1 = max(texture1.a + a1 - ma, 0.0);
    float b2 = max(texture2.a + a2 - ma, 0.0);

    return (texture1.rgb * b1 + texture2.rgb * b2) / (b1 + b2);
}

vec2 getBaboTexture(int n, in vec2 textureUV) {
	textureUV.x = u_textureUvs[n].x + (u_textureUvs[n].y - u_textureUvs[n].x) * textureUV.x;
	textureUV.y = u_textureUvs[n].z + (u_textureUvs[n].w - u_textureUvs[n].z) * textureUV.y;
	return textureUV;
}

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
	vec4 specularColor0 = texture2D(u_specularityAtlas, texture0UV);
	
	vec2 texture1UV = getBaboTexture(1, finalTextureUV);
	vec4 diffuseColor1 = texture2D(u_diffuseAtlas, texture1UV);
	vec4 normalColor1 = normalize(texture2D(u_normalAtlas, texture1UV).rgba * 2.0 - 1.0);
	vec4 specularColor1 = texture2D(u_specularityAtlas, texture1UV);
	
	// Init final parameters
	vec3 normal;
	vec4 diffuse;
	vec4 specular;
	vec3 lightSpecular = vec3(0.0);
	vec3 lightDiffuse = v_lightDiffuse;
	
	// We get alpha map
	vec4 alphaColor = texture2D(u_alphaMap, v_alphaMapUV);
	float alphaIntensity = (alphaColor.r + alphaColor.g + alphaColor.b) / 3.0;
	
	// Full texture
	alphaIntensity = 0.0;
	if( alphaIntensity == 0.0 ) {
		normal = vec3(normalColor0);
		normal = normalize((v_tangent * normal.x) + (v_binormal * normal.y) + (v_normal * normal.z));
		
		diffuse = vec4(diffuseColor0);
		specular = vec4(specularColor0);
	}
	
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
		lightDiffuse += u_pointLights[i].color * (NdotL * falloff);
		float halfDotView = clamp(dot(normal, normalize(lightDir + v_viewVec)), 0.0, 2.0);
		lightSpecular += u_pointLights[i].color * clamp(NdotL * pow(halfDotView, u_shininess) * falloff, 0.0, 2.0);
	}
	
	diffuse.rgb *= lightDiffuse;
	specular.rgb *= lightSpecular;
	gl_FragColor = vec4((diffuse.rgb + specular.rgb)*getShadow(), 1.0);
}