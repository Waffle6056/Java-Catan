package CardStructure.RenderingStuff.TextRenderer;
import org.joml.Vector2f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.*;
import org.lwjgl.util.freetype.*;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.system.MemoryStack.stackPush;


public class Text {
    public static HashMap<Character, CharacterTexture> Map = new HashMap<>();
    public static void initialize(String fontFile){
        long library, face;
        //System.out.println("1");
        try ( MemoryStack stack = stackPush() ) {
            PointerBuffer ft = stack.mallocPointer(1);
            System.out.println(FreeType.FT_Init_FreeType(ft));

            PointerBuffer ftface = stack.mallocPointer(1);
            library = ft.get();
            System.out.println(FreeType.FT_New_Face(library, fontFile, 0, ftface));

            face = ftface.get();
        }
        //System.out.println("2");
        FT_Face ftFace = FT_Face.create(face);
        //System.out.println(face);
        FreeType.nFT_Set_Pixel_Sizes(face,0,48);
        glPixelStorei(GL_UNPACK_ALIGNMENT,1);
        //System.out.println("3");
        for (char i = 0; i < 128; i++) {
            FreeType.FT_Load_Char(ftFace, i, FreeType.FT_LOAD_RENDER);

            int TextureAddress = glGenTextures();
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, TextureAddress);

            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();

            glTexImage2D(
                GL_TEXTURE_2D,
                    0,
                    GL_RED,
                    bitmap.width(),
                    bitmap.rows(),
                    0,
                    GL_RED,
                    GL_UNSIGNED_BYTE,
                    bitmap.buffer(bitmap.sizeof()) // potential issue
            );

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);

            Map.put(i, new CharacterTexture(
                    TextureAddress,
                    new Vector2f(bitmap.width(), bitmap.rows()),
                    new Vector2f(glyphSlot.bitmap_left(), glyphSlot.bitmap_top()),
                    glyphSlot.advance().x()
                    )
            );
        }
        //System.out.println("4");
        FreeType.FT_Done_Face(ftFace);
        FreeType.FT_Done_FreeType(library);
        //System.out.println("5");

    }
}
