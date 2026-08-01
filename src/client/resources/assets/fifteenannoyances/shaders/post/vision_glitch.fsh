#version 150

uniform sampler2D InSampler;
uniform float Time;
uniform vec2 InSize;

in vec2 texCoord;

out vec4 fragColor;

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    vec2 uv = texCoord;

    // horizontal glitch slices that slide around
    float slices = 14.0;
    float slice = floor(uv.y * slices);
    float slicePhase = floor(Time * 1.2);
    float strength = hash21(vec2(slice, slicePhase));
    float band = smoothstep(0.85, 1.0, strength);
    uv.x = fract(uv.x + (strength - 0.5) * 0.12 * band);

    // constant chromatic aberration
    float chroma = 0.0018 + 0.0012 * sin(Time * 6.0);
    float r = texture(InSampler, uv + vec2(chroma, 0.0)).r;
    float g = texture(InSampler, uv).g;
    float b = texture(InSampler, uv - vec2(chroma, 0.0)).b;
    vec4 col = vec4(r, g, b, 1.0);

    // random pixel scramble flicker
    float scramble = hash21(vec2(floor(uv.x * 48.0), floor(uv.y * 48.0)) + floor(Time * 4.0));
    if (scramble > 0.9) {
        col.rgb = col.bgr;
    }

    // channel rotation: every few seconds the palette shifts, hot colors go blue
    float cycle = fract(Time * 0.16);
    if (cycle >= 0.3 && cycle < 0.55) {
        col.rgb = col.brg;
    } else if (cycle >= 0.55 && cycle < 0.8) {
        col.rgb = col.grb;
    } else if (cycle >= 0.8) {
        col.rgb = col.bgr;
    }

    fragColor = col;
}
