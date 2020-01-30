#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

// it uses the default vertex stage for texture shaders. As a consequence of this, and since the varying variables are first
// declared in the vertex stage, the fragment shader has to follow the varying names adopted in the default shader.
// In this case, the varying variables for the fragment color and texture coordinate must be named vertColor and
// vertTexCoord, respectively.
uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

// Convolution filters are also possible to implement in the fragment shader.
// Given the texture coordinates of a fragment, vertTexCoord, the neighboring pixels in the
// texture (also called "texels") can be sampled using the texOffset uniform. This uniform is set
// automatically by Processing and contains the vector (1/width, 1/height), with width and height
// being the resolution of the texture. These values are precisely the offsets along the horizontal
// and vertical directions needed to sample the color from the texels around vertTexCoord.st.
// For example, vertTexCoord.st + vec2(texOffset.s, 0) is the texel exactly one position to the right.
// The following GLSL codes shows the implementation of a standard edge detection and emboss filters:
void main() {
    vec2 tc0 = vertTexCoord.st + vec2(-texOffset.s, -texOffset.t);
    vec2 tc1 = vertTexCoord.st + vec2(0.0, -texOffset.t);
    vec2 tc2 = vertTexCoord.st + vec2(+texOffset.s, -texOffset.t);
    vec2 tc3 = vertTexCoord.st + vec2(-texOffset.s, 0.0);
    vec2 tc4 = vertTexCoord.st + vec2(0.0, 0.0);
    vec2 tc5 = vertTexCoord.st + vec2(+texOffset.s, 0.0);
    vec2 tc6 = vertTexCoord.st + vec2(-texOffset.s, +texOffset.t);
    vec2 tc7 = vertTexCoord.st + vec2(0.0, +texOffset.t);
    vec2 tc8 = vertTexCoord.st + vec2(+texOffset.s, +texOffset.t);

    vec4 col0 = texture2D(texture, tc0);
    vec4 col1 = texture2D(texture, tc1);
    vec4 col2 = texture2D(texture, tc2);
    vec4 col3 = texture2D(texture, tc3);
    vec4 col4 = texture2D(texture, tc4);
    vec4 col5 = texture2D(texture, tc5);
    vec4 col6 = texture2D(texture, tc6);
    vec4 col7 = texture2D(texture, tc7);
    vec4 col8 = texture2D(texture, tc8);

    vec4 sum = 8.0 * col4 - (col0 + col1 + col2 + col3 + col5 + col6 + col7 + col8);
    gl_FragColor = vec4(sum.rgb, 1.0) * vertColor;
}