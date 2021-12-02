#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

uniform float u_time;
uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform vec3 u_color;

void main(void) {
    vec2 p = -1.0 + 2.0 * gl_FragCoord.xy / u_resolution.xy;
    vec2 m = -1.0 + 2.0 * u_mouse.xy / u_resolution.xy;

    float a1 = atan(p.y - m.y, p.x - m.x);
    float r1 = sqrt(dot(p - m, p - m));
    float a2 = atan(p.y + m.y, p.x + m.x);
    float r2 = sqrt(dot(p + m, p + m));

    vec2 uv;
    uv.x = 0.2 * u_time + (r1 - r2) * 0.25;
    uv.y = sin(2.0 * (a1 - a2));

    float w = r1 * r2 * 0.8;
    vec3 col = texture2D(texture, 0.5 - 0.495 * uv).xyz * u_color;

    gl_FragColor = vec4(col / (0.1 + w), 1.0);
}