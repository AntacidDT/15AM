#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = vec2(1.0 - texCoord.x, texCoord.y);
    fragColor = texture(InSampler, uv);
}
