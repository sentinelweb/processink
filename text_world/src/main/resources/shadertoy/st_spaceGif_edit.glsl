// Space Gif by Martijn Steinrucken aka BigWings - 2019
// Email:countfrolic@gmail.com Twitter:@The_ArtOfCode
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// Original idea from:
// https://boingboing.net/2018/12/20/bend-your-spacetime-continuum.html
//

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

void main() {

    vec2 uv = (gl_FragCoord.xy-u_resolution.xy*.5)/u_resolution.y;

    uv *= mat2(.707, -.707, .707, .707);
    uv *= 15.;

    vec2 gv = fract(uv)-.5;
    vec2 id = floor(uv);

    float m = 0.;
    float t;

    for (float y=-1.; y<=1.; y++) {
        for (float x=-1.; x<=1.; x++) {
            vec2 offs = vec2(x, y);

            t = -u_time+length(id-offs)*.2;
            float r = mix(.4, 1.5, sin(t)*.5+.5);
            float c = smoothstep(r, r*.9, length(gv+offs));
            m = m*(1.-c) + c*(1.-m);
        }
    }
    float tm = sin(u_time*15*gv);
    gl_FragColor = vec4(m*.5, m*sin(u_time*3)*0.4, m*sin(u_time*7)*0.7, m);
}