#version 150

uniform sampler2D InSampler;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 col = texture(InSampler, texCoord);
    float amt = 0.85 + 0.15 * sin(Time * 3.0);
    fragColor = vec4(mix(col.rgb, vec3(1.0) - col.rgb, amt), col.a);
}
