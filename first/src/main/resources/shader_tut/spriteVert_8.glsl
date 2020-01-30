uniform mat4 projection;
uniform mat4 modelview;

uniform float weight;

attribute vec4 position;
attribute vec4 color;
attribute vec2 offset;

varying vec4 vertColor;
varying vec2 texCoord;// varying, so it will be interpolated over all the fragments in the square

// In this example, we explicitly set the texture sampler from the sketch using PShader.set() function, which simply
// takes the PImage object encapsulating the texture as the argument. The use of this sampler in the fragment shader
// is identical to what we saw earlier in the texture shaders. However, texture sampling requires texture
// coordinates, and Processing doesn't send any to the shader because the default points are never textured.
// Therefore, we need to calculate the texture coordinates manually in the vertex shader. Given the displacement
// values contained in the offset attribute, the texture coordinates for each corner can be computed easily by
// noting that the offsets range from -weight/2 to +weight/2 along each direction
void main() {
    vec4 pos = modelview * position;
    vec4 clip = projection * pos;

    gl_Position = clip + projection * vec4(offset, 0, 0);

    //maps the offset range onto the (0, 1) interval needed for texturing
    texCoord = (vec2(0.5) + offset / weight);

    vertColor = color;
}