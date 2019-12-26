// https://www.shadertoy.com/view/ltfczM
// (c) 2017 Andrew Baldwin (twitter:baldand) - All rights reserved
#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
void main(){
//void mainImage( out vec4 fragColor, in vec2 fragCoord )
//{
    float scale = .007;
    float rise_speed = 100.0;
    float density = .5+.5*abs(fract(u_time*.01)*2.-1.);
    float layer_speed_scale = 1.0;

    vec2 uv = gl_FragCoord.xy * vec2(2.,2.);
    uv -= u_resolution.xy*vec2(1.,1.);
    vec2 x = uv;
    x *= .001;
    uv.y -= u_time*rise_speed;
    vec2 a = uv * scale;
    float s = fract(x.y*.5);
    s += s - 1.;
    a.x *= 3.+((abs(s)-.5)*3.);
    float w = fract(u_time*.3+.9*a.y*.1);
    w += w - 1.;
    w *= 1.;
    float c = fract(a.y*.5);
    c += c - 1.;
    c *= .2;
    a.x += a.y * abs(w) * .3 * abs(c);
    vec2 i = floor(a);
    float it = fract(u_time * (i.x+10.)*.03);
    it += it - 1.;
    a.y += ((1.+i.x)*(1.+i.x)*.3213+.2*abs(it)); // Wave pattern
    a.y -= u_time*layer_speed_scale*fract(abs(i.x*.312)); // Multi speed "layers"
    a.y *= .5+.5*fract(i.x*.5);
    a.y *= 2.;
    a.x += 0.5; // ?
    vec2 i2 = floor(a+.5);
    vec2 f = fract(a);
    f += f - 1.;
    f = abs(f);
    float v = step(density,fract((fract(i2.y*.1)+fract(i.x*.01))*63.232));
    float e = v*smoothstep(.5+.2*fract(-i.x*.3+.5),.99,f.x) * smoothstep(.75+.14*fract(i.x*.25+.23),.99,f.y);
    vec3 col = mix(vec3(1.,.8,.0),vec3(.5,.05,.05),clamp(x.y*2.+1.+fract(i.x*.3),0.,1.));
    vec3 bg = mix(vec3(.03,.01,.08),vec3(.0,.0,.0),x.y*5.);
    //e = f.y; // Uncomment to see underlying field structure
    col = mix(bg,e*col,e);
    gl_FragColor = vec4(col,1.);
}