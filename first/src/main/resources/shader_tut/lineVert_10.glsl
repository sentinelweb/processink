uniform mat4 transform;
uniform vec4 viewport;

attribute vec4 position;
attribute vec4 color;
attribute vec4 direction;// connects the line point to the opposite point along the quad

varying vec4 vertColor;

// Lines are drawn by the P3D renderer as a sequence of quads, but these quads need to screen-facing.
// The method applied to make the quads screen-facing is similar to the one used for points:
// each line vertex has an associated attribute variable called direction that contains the vector
// connecting the current point to the opposite point along the quad, as well as the thickness of the line,
// calculated from the stroke weight. The tangent vector is simply the direction vector normalized,
// and it is used to compute the offset along the normal direction:


// The clipToWindow() function in the vertex shader converts the clip argument to viewport coordinates in
// pixel units, to make sure that the tangent vector is effectively contained inside the screen
vec3 clipToWindow(vec4 clip, vec4 viewport) {
    vec3 dclip = clip.xyz / clip.w;
    vec2 xypos = (dclip.xy + vec2(1.0, 1.0)) * 0.5 * viewport.zw;
    return vec3(xypos, dclip.z * 0.5 + 0.5);
}

void main() {
    vec4 clip0 = transform * position;
    //  note how the position of the opposite point in the line quad is calculated from the current point
    vec4 clip1 = clip0 + transform * vec4(direction.xyz, 0);
    float thickness = direction.w;

    vec3 win0 = clipToWindow(clip0, viewport);
    vec3 win1 = clipToWindow(clip1, viewport);
    // The tangent vector is simply the direction vector normalized
    vec2 tangent = win1.xy - win0.xy;

    vec2 normal = normalize(vec2(-tangent.y, tangent.x));
    vec2 offset = normal * thickness;
    gl_Position.xy = clip0.xy + offset.xy;
    gl_Position.zw = clip0.zw;
    vertColor = color;
}