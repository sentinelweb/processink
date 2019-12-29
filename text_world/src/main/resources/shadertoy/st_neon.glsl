

#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

// https://www.shadertoy.com/view/ls3Xzn
void main(){
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;
    float s1 = 0.5+0.5*sin(u_time+uv.x*3.1415*(sin(u_time)+4.0));
    float s2 = 0.5+0.25*sin(u_time+uv.x*3.1415*(sin(u_time)*2.0+2.0));
    float r = pow(1.0-sqrt( abs(uv.y-s1)),1.5 );
    float g = pow(1.0-sqrt( abs(uv.y-s2)),1.5 );
    float b = 1.5*(r+g);
    gl_FragColor = vec4( r,g,b,1 );
}