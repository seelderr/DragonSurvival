#version 150

uniform sampler2D SkinTexture;
uniform float HueVal;
uniform float SatVal;
uniform float BrightVal;
uniform float Colorable;
uniform float Glowing;

in vec2 texCoord;

out vec4 fragColor;

vec3 getHSB(vec3 color) {
    float hue, saturation, brightness;
    vec3 hsbvals = vec3(0.0, 0.0, 0.0);

    float cmax = max(color.r, max(color.g, color.b));
    float cmin = min(color.r, min(color.g, color.b));
    brightness = cmax;

    brightness = cmax;
    if (cmax != 0.0) {
        saturation = (cmax - cmin) / cmax;
    } else {
        saturation = 0.0;
    }

    if (saturation == 0.0) {
        hue = 0.0;
    } else {
        float redc = (cmax - color.r) / (cmax - cmin);
        float greenc = (cmax - color.g) / (cmax - cmin);
        float bluec = (cmax - color.b) / (cmax - cmin);
        if (color.r == cmax) {
            hue = bluec - greenc;
        } else if (color.g == cmax) {
            hue = 2.0 + redc - bluec;
        } else {
            hue = 4.0 + greenc - redc;
        }
        hue = hue / 6.0;

        if (hue < 0.0) {
            hue = hue + 1.0;
        }
    }
    return vec3(hue, saturation, brightness);
}

vec3 getRGB(vec3 hsb) {
    float hue = hsb.x;
    float saturation = hsb.y;
    float brightness = hsb.z;
    float r = 0.0, g = 0.0, b = 0.0;

    if (saturation == 0.0) {
        r = g = b = brightness;
    } else {
        float h = mod(hue, 1.0) * 6.0;
        float f = h - floor(h);
        float p = brightness * (1.0 - saturation);
        float q = brightness * (1.0 - saturation * f);
        float t = brightness * (1.0 - saturation * (1.0 - f));
        int hi = int(h);

        if (hi == 0) {
            r = brightness;
            g = t;
            b = p;
        } else if (hi == 1) {
            r = q;
            g = brightness;
            b = p;
        } else if (hi == 2) {
            r = p;
            g = brightness;
            b = t;
        } else if (hi == 3) {
            r = p;
            g = q;
            b = brightness;
        } else if (hi == 4) {
            r = t;
            g = p;
            b = brightness;
        } else if (hi == 5) {
            r = brightness;
            g = p;
            b = q;
        }
    }

    return vec3(r, g, b);
}

vec3 getHueAdjustedColor(vec4 texColor) {
    if(texColor.a == 0.0 || Colorable < 0.5) {
        return texColor.rgb;
    }

    vec3 hsb = getHSB(texColor.rgb);

    if(Glowing > 0.5 && hsb.r == 0.5 && hsb.g == 0.5){
        return texColor.rgb;
    }

    hsb.r = hsb.r + HueVal;
    hsb.g = mix(hsb.g, SatVal > 0.5 ? 1.0 : 0.0, abs(SatVal - 0.5) * 2.0);
    hsb.b = mix(hsb.b, BrightVal > 0.5 ? 1.0 : 0.0, abs(BrightVal - 0.5) * 2.0);

    return getRGB(hsb);
}

void main() {
    vec4 texColor = texture(SkinTexture, texCoord);
    fragColor = vec4(getHueAdjustedColor(texColor), texColor.a);
}
