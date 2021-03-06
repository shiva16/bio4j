package com.bio4j.model.uniprot.edges;

import com.bio4j.model.uniprot.UniProtGraph;
import com.bio4j.model.uniprot.vertices.InterPro;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.angulillos.UntypedGraph;

/**
 * Created by ppareja on 7/28/2014.
 */
public final class ProteinInterPro <I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends
		UniProtGraph.UniProtEdge<
				Protein<I, RV, RVT, RE, RET>, UniProtGraph<I, RV, RVT, RE, RET>.ProteinType,
				ProteinInterPro<I, RV, RVT, RE, RET>, UniProtGraph<I, RV, RVT, RE, RET>.ProteinInterProType,
				InterPro<I, RV, RVT, RE, RET>, UniProtGraph<I, RV, RVT, RE, RET>.InterProType,
				I, RV, RVT, RE, RET
				> {

	public ProteinInterPro(RE edge, UniProtGraph<I, RV, RVT, RE, RET>.ProteinInterProType type) {

		super(edge, type);
	}

	@Override
	public ProteinInterPro<I, RV, RVT, RE, RET> self() {
		return this;
	}
}