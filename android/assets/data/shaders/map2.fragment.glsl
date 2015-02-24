#ifdef GL_ES
precision highp float;
#endif

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
uniform sampler2D u_specularityAtlas;
uniform sampler2D u_alphaMap;
uniform sampler2D u_shadowTexture;
uniform vec2 u_tillSize;
uniform float u_shadowPCFOffset;
//uniform vec3 u_lightDirection;

varying vec2 v_diffuseUV;
varying vec3 v_shadowMapUv;
varying vec2 v_alphaMapUV;
varying vec3 v_lightVec; 
varying vec3 v_halfVec;


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
	
	// We get alpha map
	vec4 alphaColor = texture2D(u_alphaMap, v_alphaMapUV);
	float alphaIntensity = (pn.r + pn.g + pn.b) / 3.0;
	
	// Textures
	vec2 texture0UV = getBaboTexture(0, finalTextureUV);
	vec4 diffuseColor0 = texture2D(u_diffuseAtlas, texture0UV);
	vec4 normalColor0 = texture2D(u_normalAtlas, texture0UV);
	vec4 specularColor0 = texture2D(u_specularityAtlas, texture0UV);
	
	// Global
	float distanceLight = dot(v_lightVec, v_lightVec);
	float radius = 0.01;
	float lightIntensity = 2.0;
	float attenuation = clamp(1.0 - radius * sqrt(distanceLight), 0.0, 1.0);
	vec3 lightDirection = v_lightVec * (inversesqrt(distanceLight) * lightIntensity);
	vec3 normal = normalize(normalColor0.xyz * 2.0 - 1.0);
	vec4 diffuseMaterial = vec4(diffuseColor0);
	vec4 specularMaterial = vec4(specularColor0);
	
	// Ambient
	vec4 ambientMaterial = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 ambientLight = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 ambient = ambientMaterial * ambientLight;
	
	// Diffuse
	vec4 diffuseLight = vec4(0.3, 1.0, 0.3, 1.0);
	float lamberFactor = max(dot(lightDirection, normal), 0.0);
	vec4 diffuse = diffuseMaterial * diffuseLight * lamberFactor;
	
	// Specular
	vec4 specularLight = vec4(1.0, 0.0, 0.0, 1.0);
	float shininess = pow(max(dot(v_halfVec, normal), 0.0), 2.0);
	vec4 specular = specularMaterial * specularLight * shininess;
	
	// Final color
	vec4 finalColor = ambient + (diffuse + specular) * getShadow(); // * shadow
	
	gl_FragColor = finalColor;	
	return;
	
	/*vec2 texture1UV = getBaboTexture1(finalTextureUV);
	
	// Intensity
	vec4 pn = texture2D(u_alphaMap, v_alphaMapUV);
	float intensity = (pn.r + pn.g + pn.b) / 3.0;
	
	vec4 color0 = texture2D(u_diffuseTexture, texture0UV);
	vec4 color1 = texture2D(u_diffuseTexture, texture1UV);
	
	vec4 result = vec4(baboMix(color0, intensity, color1, 1.0 - intensity), 1.0);
	
	gl_FragColor.rgb = getShadow() * result.rgb;*/
	//gl_FragColor = result;
}