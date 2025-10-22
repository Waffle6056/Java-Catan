package RenderingStuff;

import org.joml.Vector2f;

public class CharacterTexture {
    int TextureAddress;
    Vector2f Size;
    Vector2f Bearing;
    long Advance;
    public CharacterTexture(int TextureAddress, Vector2f Size, Vector2f Bearing, long Advance){
        this.TextureAddress = TextureAddress;
        this.Size = Size;
        this.Bearing = Bearing;
        this.Advance = Advance;
    }
}
