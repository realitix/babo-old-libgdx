#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;

float length2(vec2 p) { return dot(p, p); }

float noise(vec2 p){
	return fract(sin(fract(sin(p.x) * (4313.13311)) + p.y) * 3131.0011);
}

float worley(vec2 p) {
	float d = 1e30;
	for (int xo = -1; xo <= 1; ++xo)
	for (int yo = -1; yo <= 1; ++yo) {
		vec2 tp = floor(p) + vec2(xo, yo);
		d = min(d, length2(p - tp - vec2(noise(tp))));
	}
	return 3.*exp(-4.*abs(2.*d - 1.));
}

float fworley(vec2 p) {
	return sqrt(sqrt(sqrt(
		pow(worley(p - u_time), 2.) *
		worley(p*2. + 1.3 + u_time*.5) *
		worley(p*4. + 2.3 + u_time*-.25) *
		worley(p*8. + 3.3 + u_time*.125) *
		worley(p*32. + 4.3 + u_time*.125) *
		sqrt(worley(p * 64. + 5.3 + u_time * -.0625)) *
		sqrt(sqrt(worley(p * -128. + 7.3))))));
}

void main() {
	vec2 uv = gl_FragCoord.xy /u_resolution.xy;
	float t = fworley(uv * u_resolution.xy / 1800.);
	t *= exp(-length2(abs(2.*uv - 1.)));
	float r = length(abs(2.*uv - 1.) * u_resolution.xy);
	gl_FragColor = vec4(t * vec3(1.8, 1.8*t, .1 + pow(t, 2.-t)), 1.);
}