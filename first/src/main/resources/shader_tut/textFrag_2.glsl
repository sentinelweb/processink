#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

varying vec4 vertColor;
varying vec4 vertTexCoord;

//void main() {
//    gl_FragColor = texture2D(texture, vertTexCoord.st) * vertColor;
//}

// Implementing a pixelate effect becomes very easy at the level of the fragment shader.
// All we need to do is to modify the texture coordinate values, vertTexCoord.st, so that they
// are binned within a given number of cells, in this case 50:
void main() {
    int si = int(vertTexCoord.s * 50.0);
    int sj = int(vertTexCoord.t * 50.0);
    gl_FragColor = texture2D(texture, vec2(float(si) / 50.0, float(sj) / 50.0)) * vertColor;
}