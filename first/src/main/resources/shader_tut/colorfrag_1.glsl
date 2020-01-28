#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying vec4 vertColor;

void main() {
    //gl_FragColor = vertColor;
    gl_FragColor = vec4(vec3(1) - vertColor.xyz, 1);// inverts the color & 1 for  alpha
}