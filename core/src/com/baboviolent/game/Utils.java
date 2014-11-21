package com.baboviolent.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider.FileTextureProvider;

public class Utils {
	
	public static Model mergeModelDataToModel(ModelData modelData, Model model) {
		
		/**
		 * Add Mesh and MeshPart
		*/
		for (ModelMesh modelMesh : modelData.meshes) {
			int numIndices = 0;
			for (ModelMeshPart part : modelMesh.parts) {
				numIndices += part.indices.length;
			}
			VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
			int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);
	
			Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
			model.meshes.add(mesh);
			model.manageDisposable(mesh);
	
			BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
			int offset = 0;
			mesh.getIndicesBuffer().clear();
			for (ModelMeshPart part : modelMesh.parts) {
				MeshPart meshPart = new MeshPart();
				meshPart.id = part.id;
				meshPart.primitiveType = part.primitiveType;
				meshPart.indexOffset = offset;
				meshPart.numVertices = part.indices.length;
				meshPart.mesh = mesh;
				mesh.getIndicesBuffer().put(part.indices);
				offset += meshPart.numVertices;
				model.meshParts.add(meshPart);
			}
			mesh.getIndicesBuffer().position(0);
		}
		
		/**
		 * Add Materials
		*/
		
		for (ModelMaterial mtl : modelData.materials) {
			TextureProvider textureProvider = new FileTextureProvider();
			Material result = new Material();
			result.id = mtl.id;
			if (mtl.ambient != null) result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
			if (mtl.diffuse != null) result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
			if (mtl.specular != null) result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
			if (mtl.emissive != null) result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
			if (mtl.reflection != null) result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
			if (mtl.shininess > 0f) result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
			if (mtl.opacity != 1.f) result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));
	
			ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	
			// FIXME uvScaling/uvTranslation totally ignored
			if (mtl.textures != null) {
				for (ModelTexture tex : mtl.textures) {
					Texture texture;
					if (textures.containsKey(tex.fileName)) {
						texture = textures.get(tex.fileName);
					} else {
						texture = textureProvider.load(tex.fileName);
						textures.put(tex.fileName, texture);
						model.manageDisposable(texture);
					}
	
					TextureDescriptor descriptor = new TextureDescriptor(texture);
					descriptor.minFilter = texture.getMinFilter();
					descriptor.magFilter = texture.getMagFilter();
					descriptor.uWrap = texture.getUWrap();
					descriptor.vWrap = texture.getVWrap();
					
					float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
					float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
					float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
					float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;
					
					switch (tex.usage) {
						case ModelTexture.USAGE_DIFFUSE:
							result.set(new TextureAttribute(TextureAttribute.Diffuse, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						case ModelTexture.USAGE_SPECULAR:
							result.set(new TextureAttribute(TextureAttribute.Specular, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						case ModelTexture.USAGE_BUMP:
							result.set(new TextureAttribute(TextureAttribute.Bump, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						case ModelTexture.USAGE_NORMAL:
							result.set(new TextureAttribute(TextureAttribute.Normal, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						/*case ModelTexture.USAGE_AMBIENT:
							result.set(new TextureAttribute(TextureAttribute.Ambient, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						case ModelTexture.USAGE_EMISSIVE:
							result.set(new TextureAttribute(TextureAttribute.Emissive, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;
						case ModelTexture.USAGE_REFLECTION:
							result.set(new TextureAttribute(TextureAttribute.Reflection, descriptor, offsetU, offsetV, scaleU, scaleV));
							break;*/
					}
				}
			}
			
			model.materials.add(result);
		}
		
		return model;
	}
}