package com.bio4j.model.uniprot_enzymedb.edges;

import com.bio4j.model.enzymedb.EnzymeDBGraph;
import com.bio4j.model.enzymedb.vertices.Enzyme;
import com.bio4j.model.uniprot.UniProtGraph;
import com.bio4j.model.uniprot.vertices.Protein;
import com.bio4j.model.uniprot_enzymedb.UniProtEnzymeDBGraph;
import com.bio4j.angulillos.UntypedGraph;

/**
 * @author <a href="mailto:ppareja@era7.com">Pablo Pareja Tobes</a>
 */
public final class EnzymaticActivity<I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends
		UniProtEnzymeDBGraph.UniProtEnzymeDBEdge<
				// src
				Protein<I, RV, RVT, RE, RET>,
				UniProtGraph<I, RV, RVT, RE, RET>.ProteinType,
				UniProtGraph<I, RV, RVT, RE, RET>,
				// edge
				EnzymaticActivity<I, RV, RVT, RE, RET>,
				UniProtEnzymeDBGraph<I, RV, RVT, RE, RET>.EnzymaticActivityType,
				//tgt
				Enzyme<I, RV, RVT, RE, RET>,
				EnzymeDBGraph<I, RV, RVT, RE, RET>.EnzymeType,
				EnzymeDBGraph<I, RV, RVT, RE, RET>,
				// raw stuff
				I, RV, RVT, RE, RET
				> {

	public EnzymaticActivity(RE edge, UniProtEnzymeDBGraph<I, RV, RVT, RE, RET>.EnzymaticActivityType type) {

		super(edge, type);
	}

	@Override
	public EnzymaticActivity<I, RV, RVT, RE, RET> self() {
		return this;
	}
}