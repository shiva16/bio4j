package com.bio4j.model.uniprot.vertices;

import com.bio4j.model.uniprot.UniProtGraph;
import com.bio4j.model.uniprot.edges.ReferenceSubmission;
import com.bio4j.model.uniprot.edges.SubmissionDB;
import com.bio4j.angulillos.UntypedGraph;

import java.util.Optional;

/**
 * Created by ppareja on 7/23/2014.
 */
public final class Submission <I extends UntypedGraph<RV, RVT, RE, RET>, RV, RVT, RE, RET>
		extends UniProtGraph.UniProtVertex<
		Submission<I, RV, RVT, RE, RET>,
		UniProtGraph<I, RV, RVT, RE, RET>.SubmissionType,
		I, RV, RVT, RE, RET
		>  {

	public Submission(RV vertex, UniProtGraph<I, RV, RVT, RE, RET>.SubmissionType type) {
		super(vertex, type);
	}

	@Override
	public Submission<I, RV, RVT, RE, RET> self() {
		return this;
	}

	// properties
	public String title() {
		return get(type().title);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////

	// relationships

	// submissionDB
	// outgoing
	public Optional<SubmissionDB<I, RV, RVT, RE, RET>> submissionDB_out(){
		return outOneOptional(graph().SubmissionDB());
	}
	public Optional<DB<I, RV, RVT, RE, RET>> submissionDB_outV(){
		return outOneOptionalV(graph().SubmissionDB());
	}

	// referenceSubmission
	// ingoing
	public ReferenceSubmission<I, RV, RVT, RE, RET> referenceSubmission_in(){   return inOne(graph().ReferenceSubmission());}
	public Reference<I, RV, RVT, RE, RET> referenceSubmission_inV(){ return inOneV(graph().ReferenceSubmission());}
}
