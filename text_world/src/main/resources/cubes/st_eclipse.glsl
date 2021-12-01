// https://www.shadertoy.com/view/NtK3RV
#ifdef GL_ES
precision mediump float;
#endif
uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;
uniform vec3 u_color;

mat2x2 rotate(float a)
{
    float c = cos(a);
    float s = sin(a);

    return mat2x2(c, s, -s, c);
}

uint wang_hash(uint seed)
{
    seed = uint(seed ^ uint(61)) ^ uint(seed >> uint(16));
    seed *= uint(9);
    seed = seed ^ (seed >> 4);
    seed *= uint(0x27d4eb2d);
    seed = seed ^ (seed >> 15);

    return seed;
}

// Semi-random float [0, 1) from 1D position
float randomFloat(float state)
{
    return float(wang_hash(uint(state))) / 4294967296.0;
}

// Semi-random floats from 2D position
vec2 randomFloat(vec2 state)
{
    return vec2(
    randomFloat(state.x + state.y * 1024.0),
    randomFloat(state.x * 512.0 + state.y * 2048.0)
    );
}

float perlinNoiseOctave(vec2 p)
{
    float offsetStep = 1.0;
    float gridSize = 10.0;

    vec2 id = floor(p * gridSize);

    // Random corners
    float upperLeft  = randomFloat(id + vec2(0.0, 0.0)).x;
    float upperRight = randomFloat(id + vec2(offsetStep, 0.0)).x;
    float lowerLeft  = randomFloat(id + vec2(0.0, offsetStep)).x;
    float lowerRight = randomFloat(id + vec2(offsetStep, offsetStep)).x;

    // Bicubic interpolation
    vec2 st = smoothstep(0.0, 1.0, fract(p * gridSize));
    float upperMix = mix(upperLeft, upperRight, st.x);
    float lowerMix = mix(lowerLeft, lowerRight, st.x);
    float finalMix = mix(upperMix, lowerMix, st.y);

    return finalMix;
}

float perlinNoise(vec2 p)
{
    float currentNoise = 0.0;

    // Add octaves
    for (float i = 0.0; i < 5.0; i += 1.0)
    currentNoise += perlinNoiseOctave(p*pow(2.0, i))/pow(2.0, i+1.0);

    // Returns value from 0 to 0.96875    
    return currentNoise;
}

vec3 getCol(vec3 viewDir)
{
    vec3 col = vec3(0.0);

    vec2 uv = viewDir.xy;
    float lenUV = length(uv);

    // Red falloff
    col = mix(u_color, col, lenUV);

    // Red shine
    float redShineRadius = lenUV + perlinNoise(vec2((atan(uv.y, uv.x) + 4.0) * 1.0, u_time * 0.01)) * 0.8 + 0.1;
    col = mix(u_color, col, clamp(redShineRadius, 0.0, 1.0));

    // Yellow shine
    float yellowHaloRadius =
    perlinNoise(vec2(cos(atan(uv.y, uv.x)), sin(atan(uv.y, uv.x))) + vec2(4.0, 2.0 + u_time * 0.05)) *
    0.02 + 0.42;
    vec3 shineCol = mix(vec3(0.95, 0.95, 0.3), vec3(0.7, 0.0, 0.0), clamp(lenUV * 7.0 - 2.7, 0.0, 1.0));
    col += shineCol * smoothstep(yellowHaloRadius, yellowHaloRadius-0.03, lenUV);

    // Overinterpolate to keep colors around origin
    col = mix(vec3(0.50, 0.98, 0.98), col, lenUV * 2.0);

    // Red sun halo
    col = mix(col, u_color, smoothstep(0.41, 0.39, lenUV));

    // Moon shadow
    float moonRadius = 0.38 + perlinNoise(vec2(sin(atan(uv.y, uv.x)) + 3.0, 0.0)) * 0.02;
    col = mix(col, vec3(0.1, 0.02, 0.02), smoothstep(moonRadius, moonRadius - 0.04, lenUV));

    // Fog
    uv *= rotate(0.3);
    col = mix(col, u_color*0.7, perlinNoise(uv + vec2(u_time * 0.05, 0.0) + vec2(4.0)) * pow(1.0 - uv.y*uv.y, 4.0) * 0.8 * min(lenUV*1.5, 1.0));
    col = mix(col, u_color*0.7, perlinNoise(uv + vec2(u_time * 0.01, 0.0) + vec2(2.0)) * pow(1.0 - uv.y*uv.y, 4.0) * 0.8 * min(lenUV*1.5, 1.0));

    return col;
}

void main()
{
    vec2 uv = (gl_FragCoord.xy - u_resolution.xy * 0.5) / u_resolution.y;

    vec3 viewDir = normalize(vec3(uv, 0.3));
    vec3 col = getCol(viewDir);

    gl_FragColor = vec4(col, 1.0);
}