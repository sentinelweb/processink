#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vertColor;
varying vec3 ecNormal;
varying vec3 direction;

void main() {
    vec3 direction = normalize(direction);
    vec3 normal = normalize(ecNormal);
    float intensity = max(0.0, dot(direction, normal));
    gl_FragColor = vec4(intensity, intensity, intensity, 1) * vertColor;
}