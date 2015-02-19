#ifdef GL_ES
precision highp float;
#endif

/**
 * Textures UV are in order U1, V1, U2, V2 with U1 and V1 the bottom left.
 * AlphaMap contain 4 channels. By default, texture0 is used
 * a -> texture1
 * r -> texture2
 * g -> texture3
 * b -> texture4
*/

uniform vec4 u_texture0UV;
uniform vec4 u_texture1UV;
uniform sampler2D u_diffuseTexture;
uniform sampler2D u_alphaMap;
uniform vec2 u_tillSize;

varying vec2 v_diffuseUV;

vec3 baboMix(vec4 texture1, float a1, vec4 texture2, float a2) {
    float depth = 0.2;
    float ma = max(texture1.a + a1, texture2.a + a2) - depth;

    float b1 = max(texture1.a + a1 - ma, 0.0);
    float b2 = max(texture2.a + a2 - ma, 0.0);

    return (texture1.rgb * b1 + texture2.rgb * b2) / (b1 + b2);
}

vec2 getBaboTexture0(in vec2 textureUV) {
	textureUV.x = u_texture0UV.x + (u_texture0UV.z - u_texture0UV.x) * textureUV.x;
	textureUV.y = u_texture0UV.y + (u_texture0UV.w - u_texture0UV.y) * textureUV.y;
	return textureUV;
}

vec2 getBaboTexture1(in vec2 textureUV) {
	textureUV.x = u_texture1UV.x + (u_texture1UV.z - u_texture1UV.x) * textureUV.x;
	textureUV.y = u_texture1UV.y + (u_texture1UV.w - u_texture1UV.y) * textureUV.y;
	return textureUV;
}


void main( void )
{
	// We till the texture
	vec2 finalTextureUV = fract(v_diffuseUV.xy / u_tillSize);
	
	vec2 texture0UV = getBaboTexture0(finalTextureUV);
	vec2 texture1UV = getBaboTexture1(finalTextureUV);
	
	// Intensity
	vec4 pn = texture2D(u_alphaMap, v_diffuseUV);
	float intensity = (pn.r + pn.g + pn.b) / 3.0;
	
	vec4 color0 = texture2D(u_diffuseTexture, texture0UV);
	vec4 color1 = texture2D(u_diffuseTexture, texture1UV);
	
	vec4 result = vec4(baboMix(color0, intensity, color1, 1.0 - intensity), 1.0);
	
	gl_FragColor = result;
}