#version 150

uniform sampler2D InSampler;
uniform sampler2D BlurSampler;
uniform sampler2D DepthSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float depth = texture(DepthSampler, texCoord).r;
    vec4 sharp = texture(InSampler, texCoord);
    vec4 blurred = texture(BlurSampler, texCoord);
    float amount = clamp((depth - 0.35) * 2.5, 0.0, 1.0);
    fragColor = vec4(mix(sharp.rgb, blurred.rgb, amount), 1.0);
}
