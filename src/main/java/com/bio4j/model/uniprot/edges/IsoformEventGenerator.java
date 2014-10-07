package com.bio4j.model.uniprot.edges;

import com.bio4j.model.uniprot.UniprotGraph;
import com.bio4j.model.uniprot.vertices.AlternativeProduct;
import com.bio4j.model.uniprot.vertices.Isoform;
import com.ohnosequences.typedGraphs.UntypedGraph;

/**
 * @author <a href="mailto:ppareja@era7.com">Pablo Pareja Tobes</a>
 */
public final class IsoformEventGenerator<I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends
		UniprotGraph.UniprotEdge<
				Isoform<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.IsoformType,
				IsoformEventGenerator<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.IsoformEventGeneratorType,
				AlternativeProduct<I, RV, RVT, RE, RET>, UniprotGraph<I, RV, RVT, RE, RET>.AlternativeProductType,
				I, RV, RVT, RE, RET
				> {

	public IsoformEventGenerator(RE edge, UniprotGraph<I, RV, RVT, RE, RET>.IsoformEventGeneratorType type) {

		super(edge, type);
	}

	@Override
	public IsoformEventGenerator<I, RV, RVT, RE, RET> self() {
		return this;
	}
}