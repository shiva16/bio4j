package com.bio4j.model.uniprot.relationships;

import com.bio4j.model.uniprot.UniprotGraph;
import com.bio4j.model.uniprot.nodes.Institute;
import com.bio4j.model.uniprot.nodes.Thesis;
import com.ohnosequences.typedGraphs.UntypedGraph;

/**
 * Created by ppareja on 7/28/2014.
 */
public final class ThesisInstitute <I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends
		UniprotGraph.UniprotEdge<
				Thesis<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.ThesisType,
				ThesisInstitute<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.ThesisInstituteType,
				Institute<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.InstituteType,
				I, RV, RVT, RE, RET
				> {

	public ThesisInstitute(RE edge, UniprotGraph<I, RV, RVT, RE, RET>.ThesisInstituteType type) {

		super(edge, type);
	}

	@Override
	public ThesisInstitute<I, RV, RVT, RE, RET> self() {
		return this;
	}
}