// Author @patriciogv - 2015
// http://patriciogonzalezvivo.com

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

// todo fix - make a cross & then mondrian painting
//https://thebookofshaders.com/07/
float cross(vec2 st, float width, float edge, float start, float end) {
    vec2 bl = 1 - smoothstep(vec2(start), vec2(start+edge), st)
    * smoothstep(vec2(width), vec2(width+edge), st)
    * smoothstep(vec2(1.0-width), vec2(1.0-width-edge), st)
    * smoothstep(vec2(1.0-end), vec2(1.0-end+edge), st);
    return bl.x * bl.y;
}

float frame(vec2 st, float width, float edge) {
    vec2 bl = smoothstep(vec2(width), vec2(width+edge), st)
            * smoothstep(vec2(1.0-width), vec2(1.0-width-edge), st);
    return bl.x * bl.y;
}

void main(){
    vec2 st = gl_FragCoord.xy/u_resolution.xy;
    vec3 color = vec3(0.0);

    float pct = frame(st, 0.1, 0.02);

    color = vec3(pct);

    gl_FragColor = vec4(color, 1.0);
}
