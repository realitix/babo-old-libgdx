package com.baboviolent.game;

import com.baboviolent.game.bullet.BulletInstance;
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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMesh;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMeshPart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
		
		/**
		 * Add Node
		*/
		for (ModelNode modelNode : modelData.nodes) {
			Node node = new Node();
			node.id = modelNode.id;
	
			if (modelNode.translation != null) node.translation.set(modelNode.translation);
			if (modelNode.rotation != null) node.rotation.set(modelNode.rotation);
			if (modelNode.scale != null) node.scale.set(modelNode.scale);
			if (modelNode.parts != null) {
				for (ModelNodePart modelNodePart : modelNode.parts) {
					MeshPart meshPart = null;
					Material meshMaterial = null;
	
					if (modelNodePart.meshPartId != null) {
						for (MeshPart part : model.meshParts) {
							if (modelNodePart.meshPartId.equals(part.id)) {
								meshPart = part;
								break;
							}
						}
					}
	
					if (modelNodePart.materialId != null) {
						for (Material material : model.materials) {
							if (modelNodePart.materialId.equals(material.id)) {
								meshMaterial = material;
								break;
							}
						}
					}

					if (meshPart != null && meshMaterial != null) {
						NodePart nodePart = new NodePart();
						nodePart.meshPart = meshPart;
						nodePart.material = meshMaterial;
						node.parts.add(nodePart);
					}
					else {
						throw new GdxRuntimeException("Invalid node: " + node.id);
					}
				}
			}
			model.nodes.add(node);
		}
		
		return model;
	}
	
	public static btConvexHullShape convexHullShapeFromModel (final Model model) {
		final Mesh mesh = model.meshes.get(0);
		btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
		// now optimize the shape
		btShapeHull hull = new btShapeHull(shape);
		hull.buildHull(shape.getMargin());
		btConvexHullShape result = new btConvexHullShape(hull);
		// delete the temporary shape
		shape.dispose();
		hull.dispose();
		return result;
	}
	
	public static Vector3 getModelDimensions(final Model model) {
	    Vector3 result = new Vector3();
	    BoundingBox bb = new BoundingBox();
        model.calculateBoundingBox(bb);
        bb.getDimensions(result);
        return result;
	}

    public static Vector3 getInstanceDimensions(final BulletInstance instance) {
	    Vector3 result = new Vector3();
	    BoundingBox bb = new BoundingBox();
        instance.calculateBoundingBox(bb);
        bb.getDimensions(result);
        return result;
	}
	
	/**
	 * Renvoie la position sur la grille en fonction de la souris
	 * la grille est sur l'axe XZ, le vecteur doit être fourni pour optimiser
	 * Car utiliser pendant une boucle'
	 */ 
	public static Vector3 getPositionFromMouse(Vector3 position, Camera camera, int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.y / ray.direction.y;
        position.set(ray.direction).scl(distance).add(ray.origin);
        return position;
	}
	
	/**
	 * Comme au dessus mais récupère les coordonnée de la souris
	 */ 
	public static Vector3 getPositionFromMouse(Vector3 position, Camera camera) {
		return getPositionFromMouse(position, camera, Gdx.input.getX(), Gdx.input.getY());
	}
}