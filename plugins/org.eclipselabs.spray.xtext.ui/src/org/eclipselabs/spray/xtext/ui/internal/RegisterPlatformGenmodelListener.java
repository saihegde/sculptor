package org.eclipselabs.spray.xtext.ui.internal;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.google.inject.Inject;

class RegisterPlatformGenmodelListener implements IResourceChangeListener {
    @Inject
    MweGenModelHelperExt          genModelHelper;
    private IResourceDeltaVisitor deltaVisitor = new IResourceDeltaVisitor() {
                                                   @Override
                                                   public boolean visit(IResourceDelta delta) throws CoreException {
                                                       if (delta.getResource() != null && delta.getResource().isAccessible()) {
                                                           IResource resource = delta.getResource();
                                                           handleResource(resource, delta.getKind());
                                                           return true;
                                                       } else {
                                                           return false;
                                                       }
                                                   }
                                               };

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                if (event.getDelta() != null) {
                    if (event.getDelta().getResource() instanceof IWorkspaceRoot) {
                        for (IResourceDelta delta : event.getDelta().getAffectedChildren()) {
                            delta.accept(deltaVisitor);
                        }
                    } else {
                        event.getDelta().accept(deltaVisitor);
                    }
                } else if (event.getResource() != null) {
                }
            } else if (event.getType() == IResourceChangeEvent.PRE_CLOSE || event.getType() == IResourceChangeEvent.PRE_DELETE) {
                event.getResource().accept(new IResourceVisitor() {

                    @Override
                    public boolean visit(IResource resource) throws CoreException {
                        handleResource(resource, IResourceDelta.REMOVED);
                        return true;
                    }
                });
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }

    }

    public void initWorkspace() throws CoreException {
        ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {
            @Override
            public boolean visit(IResource resource) throws CoreException {
                handleResource(resource, IResourceDelta.ADDED);
                return true;
            }
        });
    }

    protected void handleResource(IResource resource, int deltaKind) {
        IFile file = (IFile) resource.getAdapter(IFile.class);
        if (file != null && file.getFileExtension().equals("genmodel") && !file.getProjectRelativePath().toPortableString().contains("target")) {
            if (deltaKind == IResourceDelta.ADDED) {
                registerGenmodel(file);
            } else if (deltaKind == IResourceDelta.REMOVED) {
                unregisterGenmodel(file);
            }
        }
    }

    protected void registerGenmodel(IFile file) {
        ResourceSet rs = new ResourceSetImpl();
        Resource emfResource = rs.createResource(URI.createPlatformResourceURI((file.getFullPath().toPortableString()), true));
        try {
            emfResource.load(Collections.EMPTY_MAP);
            genModelHelper.registerGenModel(emfResource.getResourceSet(), emfResource.getURI());
            emfResource.unload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void unregisterGenmodel(IFile file) {
        ResourceSet rs = new ResourceSetImpl();
        Resource emfResource = rs.createResource(URI.createPlatformResourceURI((file.getFullPath().toPortableString()), true));
        try {
            emfResource.load(Collections.EMPTY_MAP);
            genModelHelper.unregisterGenModel(emfResource.getResourceSet(), emfResource.getURI());
            emfResource.unload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
