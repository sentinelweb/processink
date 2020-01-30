#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform float weight;

varying vec2 center;
varying vec2 normal;
varying vec4 vertColor;

void main() {
    vec2 v = gl_FragCoord.xy - center;
    // The key calculation here is the dot product between the normal vector and the vector from the center of
    // the stroke quad and the current fragment position, dot(normalize(normal), v), which will be exactly 0
    // along the spine of the quad, and so, giving alpha equal to 1.
    float alpha = 1.0 - abs(2.0 * dot(normalize(normal), v) / weight);
    gl_FragColor = vec4(vertColor.rgb, alpha);
}