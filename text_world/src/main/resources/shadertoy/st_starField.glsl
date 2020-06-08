#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

// https://www.shadertoy.com/view/ls3Xzn
// Starfield Tutorial by Martijn Steinrucken aka BigWings - 2020
// countfrolic@gmail.com
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
//
// This is the end result of a tutorial on my YouTube channel The Art of Code
//

#define NUM_LAYERS 6.

mat2 Rot(float a) {
    float s=sin(a), c=cos(a);
    return mat2(c, -s, s, c);
}

float twinkle(float freq) {
    return (sin(u_time*freq)+1)*0.1+0.2;
}
float Star(vec2 uv, float flare) {
    float d = length(uv);
    float m = .05/d;

    float rays = max(0., 1.-abs(uv.x*uv.y*1000.*twinkle(5)));
    m += rays*flare;
    uv *= Rot(3.1415/4.)*5;
    rays = max(0., 1.-abs(uv.x*uv.y*1000.*twinkle(8)));//
    m += rays*flare;

    m *= smoothstep(1., .2, d);
    return m;
}

float Hash21(vec2 p) {
    p = fract(p*vec2(123.34, 456.21));
    p += dot(p, p+45.32);
    return fract(p.x*p.y);
}

vec3 StarLayer(vec2 uv) {
    vec3 col = vec3(0);

    vec2 gv = fract(uv)-.5;
    vec2 id = floor(uv);

    for (int y=-1;y<=1;y++) {
        for (int x=-1;x<=1;x++) {
            vec2 offs = vec2(x, y);

            float n = Hash21(id+offs);// random between 0 and 1
            float size = fract(n*345.32);

            float star = Star(gv-offs-vec2(n, fract(n*34.))+.5, smoothstep(.9, 1., size)*100.6);

            vec3 color = sin(vec3(.2, .3, .9)*fract(n*2345.2)*123.2)*.5+.5;
            color = color*vec3(1, .25, 1.+size)+vec3(.2, .2, .1)*2.;

            star *= sin(u_time*15.+n*6.2831)*.5+1.;
            col += star*size*color;
        }
    }
    return col;
}

void main()
{
    vec2 uv = (gl_FragCoord.xy-.5*u_resolution.xy)/u_resolution.y;

    vec2 M = (u_mouse.xy-u_resolution.xy*.5)/u_resolution.y;

    float t = u_time*.02;

    uv += M*1.;

    uv *= Rot(t);
    vec3 col = vec3(0);

    for (float i=0.; i<1.; i+=1./NUM_LAYERS) {
        float depth = fract(i+t);

        float scale = mix(20., .5, depth);
        float fade = depth*smoothstep(1., .9, depth);
        col += StarLayer(uv*scale+i*453.2-M)*fade;
    }

    gl_FragColor = vec4(col, 1.0);
}

//
//void main(){
//    vec2 uv = gl_FragCoord.xy / u_resolution.xy;
//    float s1 = 0.5+0.5*sin(u_time+uv.x*3.1415*(sin(u_time)+4.0));
//    float s2 = 0.5+0.25*sin(u_time+uv.x*3.1415*(sin(u_time)*2.0+2.0));
//    float r = pow(1.0-sqrt( abs(uv.y-s1)),1.5 );
//    float g = pow(1.0-sqrt( abs(uv.y-s2)),1.5 );
//    float b = 1.5*(r+g);
//    gl_FragColor = vec4( r,g,b,1 );
//}