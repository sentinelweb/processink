// 'Warp Speed' by David Hoskins 2013.
// I tried to find gaps and variation in the star cloud for a feeling of structure.

// https://www.shadertoy.com/view/3dVczw
#ifdef GL_ES
precision mediump float;
#endif
uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform vec3 u_color;

void main()
{
    float time = (u_time+29.) * 60.0;

    float s = 0.0, v = 0.0;
    vec2 uv = (-u_resolution.xy + 2.0 * gl_FragCoord.xy) / u_resolution.y;
    float t = time*0.005;
    uv.x += sin(t) * 0.5;
    float si = sin(t + 2.17);// ...Squiffy rotation matrix!
    float co = cos(t);
    uv *= mat2(co, si, -si, co);
    vec3 col = vec3(0.0);
    vec3 init = vec3(0.25, 0.25 + sin(time * 0.001) * 0.4, floor(time) * 0.0008);
    for (int r = 0; r < 100; r++)
    {
        vec3 p = init + s * vec3(uv, 0.143);
        p.z = mod(p.z, 2.0);
        for (int i=0; i < 10; i++)    p = abs(p * 2.04) / dot(p, p) - 0.75;
        v += length(p * p) * smoothstep(0.0, 0.5, 0.9 - s) * .002;
        // Get a purple and cyan effect by biasing the RGB in different ways...
        col +=  vec3(v * 0.8, 1.1 - s * 0.5, .7 + v * 0.5) * v * 0.013;
        s += .01;
    }
    gl_FragColor = vec4(col, 1.0);
}