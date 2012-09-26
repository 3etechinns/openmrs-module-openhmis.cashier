package org.openmrs.module.webservices.rest.resource;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.cashier.api.IMetadataService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

public abstract class BaseRestMetadataResource<E extends OpenmrsMetadata> extends MetadataDelegatingCrudResource<E> implements IMetadataServiceResource<E> {

	@Override
	public abstract E newDelegate();
	
	@Override
	public E save(E delegate) {
		IMetadataService<E> service = Context.getService(getServiceClass());
		service.save(delegate);
		return delegate;
	}

	protected DelegatingResourceDescription getDefaultRepresentationDescription() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("retired");
		description.addProperty("retireReason");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("name");
			return description;
		}
		DelegatingResourceDescription description = getDefaultRepresentationDescription();		
		if (rep instanceof FullRepresentation) {
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
		}
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = getDefaultRepresentationDescription();
		description.removeProperty("uuid");
		description.removeProperty("retireReason");
		return description;
	}

	@Override
	public E getByUniqueId(String uniqueId) {
		IMetadataService<E> service = Context.getService(getServiceClass());
		E entity = service.getByUuid(uniqueId);
		return entity;
	}

	@Override
	public void purge(E delegate, RequestContext context)
			throws ResponseException {
		IMetadataService<E> service = Context.getService(getServiceClass());
		service.purge(delegate);		
	}
	
	@Override
	protected NeedsPaging<E> doGetAll(RequestContext context) throws ResponseException {
		IMetadataService<E> service = Context.getService(getServiceClass());
		return new NeedsPaging<E>(service.getAll(), context);
	}

	@Override
	protected AlreadyPaged<E> doSearch(String query, RequestContext context) {
		context.setRepresentation(Representation.REF);
		return new MetadataSearcher<E>(getServiceClass()).searchByName(query, context);
	}
}
