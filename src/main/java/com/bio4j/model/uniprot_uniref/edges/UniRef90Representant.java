package com.bio4j.model.uniprot_uniref.edges;


import com.bio4j.model.uniprot.UniProtGraph;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.model.uniprot_uniref.UniProtUniRefGraph;
import com.bio4j.model.uniref.UniRefGraph;
import com.bio4j.model.uniref.vertices.UniRef90Cluster;
import com.bio4j.angulillos.UntypedGraph;

/**
 * @author <a href="mailto:ppareja@era7.com">Pablo Pareja Tobes</a>
 */
public final class UniRef90Representant<I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends
		UniProtUniRefGraph.UniProtUniRefEdge<
				// src
				Protein<I, RV, RVT, RE, RET>,
				UniProtGraph<I, RV, RVT, RE, RET>.ProteinType,
				UniProtGraph<I, RV, RVT, RE, RET>,
				// edge
				UniRef90Representant<I, RV, RVT, RE, RET>,
				UniProtUniRefGraph<I, RV, RVT, RE, RET>.UniRef90RepresentantType,
				//tgt
				UniRef90Cluster<I, RV, RVT, RE, RET>,
				UniRefGraph<I, RV, RVT, RE, RET>.UniRef90ClusterType,
				UniRefGraph<I, RV, RVT, RE, RET>,
				// raw stuff
				I, RV, RVT, RE, RET
				> {

	public UniRef90Representant(RE edge, UniProtUniRefGraph<I, RV, RVT, RE, RET>.UniRef90RepresentantType type) {

		super(edge, type);
	}

	// properties
	public String proteinAccession() {
		return get(type().proteinAccession);
	}

	@Override
	public UniRef90Representant<I, RV, RVT, RE, RET> self() {
		return this;
	}
}