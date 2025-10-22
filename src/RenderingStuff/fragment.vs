#version 330 core
struct Light{
   vec3 pos;
   vec3 ambient;
   vec3 diffuse;
   vec3 specular;
};


out vec4 FragColor;

in vec2 texCoord;
in vec3 fragPos;
in vec3 normal;

uniform Light light;

uniform sampler2D diffuseMapTest;

uniform vec3 viewPos;

void main()
{
   vec3 viewDir = normalize(viewPos - fragPos);
   vec3 lightDir = normalize(light.pos - fragPos);
   vec3 reflectDir = reflect(-lightDir, normal);

   vec3 lightColor = texture(diffuseMapTest, texCoord).xyz;

   vec3 ambient = light.ambient * lightColor.rgb;

   float diffuseVal = max(dot(normalize(normal), lightDir), 0.0);
   vec3 diffuse = light.diffuse * lightColor.rgb * diffuseVal;

   float specularVal = pow(max(dot(viewDir, reflectDir), 0.0), 32);
   vec3 specular = light.specular * lightColor.rgb * specularVal;

   FragColor = vec4(ambient + diffuse + specular, texture(diffuseMapTest, texCoord).a);
}