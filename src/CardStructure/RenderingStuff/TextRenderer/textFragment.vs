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

   vec3 lightColor = texture(diffuseMapTest, texCoord).xyz;


   FragColor = vec4(1,1,1, lightColor.r);
}