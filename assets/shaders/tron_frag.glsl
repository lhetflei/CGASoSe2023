#version 330 core

in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDirpoint[10];
    vec3 lightDirspot;
    vec3 viewDir;
    vec3 normal;
} vertexData;

struct PointLight {
    vec3 position[10];
    vec3 lightColor[10];
};

struct SpotLight {
    vec3 direction;
    vec3 position;
    vec3 lightColor;
    float innerConeAngle;
    float outerConeAngle;
};

uniform SpotLight spotLight;
uniform PointLight pointLight;
uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
uniform float shininess;
uniform vec3 emit_col;
uniform float gammaValue;

out vec4 color;

// Gammakorrektur-Funktion
/*float gamma(float value, float gammaValue) {
    return pow(value, 1.0 / gammaValue);
}*/

vec3 gamma(vec3 C_linear) {
    float gammaValue = 2.2;  // Gamma value, adjust as needed
    return pow(C_linear, vec3(1.0 / gammaValue));
}

// Inverse Gammakorrektur-Funktion
vec3 invgamma(vec3 value, float gammaValue) {
    return pow(value, vec3(gammaValue));
}

vec3 brdf(vec3 n, vec3 l, vec3 v, vec3 ms, vec3 md, float k,float attenuation)
{
    //vec3 r = reflect(-l, n);
    vec3 h = normalize(l + v);//half-vector blinn-phong
    vec3 c = max(0.0, dot(n, l)) * md * attenuation;
    vec3 d = pow(max(0.0, dot(n, h)), k) * ms * attenuation;

    return c + d;
}

void main()
{
    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 normal = normalize(vertexData.normal);
    //vec3 lightDirpoint = normalize(vertexData.lightDirpoint);
    vec3 lightDirspot = normalize(vertexData.lightDirspot);

    vec4 emissiveCol = texture(material_emissive, vertexData.tc)*vec4(emit_col,1.0);
    vec4 diffuseCol = texture(material_diffuse, vertexData.tc);
    vec4 specularCol = texture(material_specular, vertexData.tc);
    color = vec4(0, 0, 0, 1);
    vec4 ambientCol = vec4(0.04, 0.04, 0.04, 1.0);

    // Gammakorrektur für diffuse, specular und emissive Farbwerte

    vec3 linearDiffuseCol = gamma(diffuseCol.xyz);
    vec3 linearSpecularCol = gamma(specularCol.xyz);
    vec3 linearEmissiveCol = gamma(emissiveCol.xyz);

    //ambient color
    color.xyz += vec3(ambientCol);

    //emissive
    color.xyz += linearEmissiveCol;

    //pointlight
    for (int i = 0; i < 10; i++) {
        vec3 lightDirpoint = normalize(vertexData.lightDirpoint[i]);
        float distance = length(vertexData.lightDirpoint[i]);
        float attenuation = 1.0 / (distance * distance);  // attenuation
        color.xyz += brdf(normal, lightDirpoint, viewDir, linearSpecularCol, linearDiffuseCol, shininess, attenuation) * pointLight.lightColor[i];

        /*vec3 lightDirpoint = normalize(vertexData.lightDirpoint[i]);
        color.xyz += brdf(normal, lightDirpoint, viewDir, linearSpecularCol, linearDiffuseCol, shininess) * pointLight.lightColor[i];*/
    }

    //color.xyz += brdf(normal, lightDirpoint, viewDir, linearSpecularCol, linearDiffuseCol, shininess) * pointLight.lightColor;

    vec3 lightDirection = normalize(spotLight.direction);
    float theta = dot(-lightDirspot, lightDirection);
    float gamma = spotLight.outerConeAngle;
    float phi = spotLight.innerConeAngle;
    float intensity = clamp((theta - gamma) / (phi - gamma), 0.0, 1.0);
    color.xyz += brdf(normal, lightDirspot, viewDir, linearSpecularCol, linearDiffuseCol, shininess,1) * spotLight.lightColor * intensity;

    // Inverse Gammakorrektur, um das Ergebnis in sRGB oder Gamma zu konvertieren

    color.xyz = invgamma(color.xyz, gammaValue);

    color.a = 1.0;
}



    //color = ambientTerm+ diffuseTerm*intensity+ diffuseTerm + specularTerm+specularTerm*intensity;


/*#version 330 core

// Input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

uniform struct PointLight {
    vec3 position;
} pointLight;

uniform struct SpotLight {
    float innerConeAngle;
    float outerConeAngle;
    vec3 direction;
    vec3 position;
} spotLight;

uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
uniform float shininess;
uniform vec3 lightColor;

// Fragment shader output
out vec4 color;

void main()
{
    vec3 normal = normalize(vertexData.normal);
    vec3 lightDir = normalize(vertexData.lightDir);



    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 reflectDir = reflect(-lightDir, normal);
    float specular = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    vec4 emissiveCol = texture(material_emissive, vertexData.tc);
    vec4 diffuseCol = texture(material_diffuse, vertexData.tc);
    vec4 specularCol = texture(material_specular, vertexData.tc);

    vec4 ambientCol = vec4(1.0, 1.0, 1.0, 1.0); // Ambient color

    vec4 lightIntensity = vec4(lightColor, 1.0); // Light intensity

    vec4 ambientTerm = emissiveCol * ambientCol;
    vec4 diffuseTerm = diffuseCol * lightIntensity * max(dot(normal, lightDir), 0.0);
    vec4 specularTerm = specularCol * lightIntensity  * pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    color = ambientTerm + diffuseTerm + specularTerm;
}

/*#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 color;
    vec2 tc;
    vec3 lightDir;
    vec3 viewDir;
    vec3 normal;
} vertexData;

uniform struct SpotLight {

    float innerConeAngle;
    float outerConeAngle;
    vec3 direction;

} spotLight;

uniform sampler2D material_emissive;
uniform sampler2D material_diffuse;
uniform sampler2D material_specular;
float shininess;
uniform vec3 lightColor;
//fragment shader output
out vec4 color;

void main(){



    vec3 normal = normalize(vertexData.normal);
    vec3 lightDir = normalize(vertexData.lightDir);
    float diffuse = max(dot(normal,lightDir),0.0);

    vec3 viewDir = normalize(vertexData.viewDir);
    vec3 reflectDir = normalize(reflect(-lightDir,normal));
    float scpecular = pow(max(dot(viewDir,reflectDir),0.0),shininess);

    vec4 emissiveCol = texture(material_emissive,vertexData.tc);
    vec4 diffuseCol = texture(material_diffuse,vertexData.tc);
    vec4 specularCol = texture(material_specular,vertexData.tc);

    color = emissiveCol * (diffuse*diffuseCol+scpecular*specularCol)* vec4(lightColor,1.0);

    //color = vec4(emissiveColor.rgb, 1.0);
}*/

