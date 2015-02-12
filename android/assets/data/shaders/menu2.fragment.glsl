#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;
uniform float u_alpha;

void main( void )
{
	float c = 1.0;
	float inten = .05;
	float t;
	float d = distance((gl_FragCoord.yy/u_resolution.yy), vec2(0.5,0.5));
	float e = distance((gl_FragCoord.xy/u_resolution.xy), vec2(0.5,0.5));	
	vec4 texColor = vec4(0.15, 0.0, 0.0, 1.0);
	vec2 v_texCoord = gl_FragCoord.xy / u_resolution;	
	vec2 p =  v_texCoord * 8.0 - vec2(20.0);
	vec2 i = p;
	
	t = (u_time * 1.0)* (1.0 - (3.0 / float(0+1)));	
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));			 
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	
	t = u_time * (1.0 - (3.0 / float(1+1)));	
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));			 
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	
	t = u_time * (1.0 - (3.0 / float(2+1)));	
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));			 
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	
	t = u_time * (1.0 - (3.0 / float(3+1)));	
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));			 
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	
	c /= 4.0;
	c = 1.46 - sqrt(c);

	texColor.rgb *= (1.0 / (1.0 - (c + 0.05)));
	texColor *= smoothstep(0.5, 0.0, d);
	texColor *= smoothstep(0.6, 0.0, e);
	texColor.rgba *= u_alpha;
	
	gl_FragColor = texColor;
}