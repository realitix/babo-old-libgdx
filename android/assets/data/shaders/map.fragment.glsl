#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;

varying vec2 v_diffuseUV;
varying float v_range;
varying vec3 v_shadowMapUv;

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

void main( void )
{
	vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);
	gl_FragColor.rgb = getShadow() * diffuse.rgb;
	
	//gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV);
	//gl_FragColor = vec4(v_diffuseUV.xy, 0.0, 1.0);
}