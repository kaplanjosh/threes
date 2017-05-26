package threes.analysis;

import java.util.*;

import org.bson.Document;

import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import threes.*;
import threes.brains.ZeroMoves;
import threes.engine.*;

public class EndGame {
//	public static HashSet<String> deads(String initialBoard, int min, int max) {
	public static List<WriteModel<Document>> deads(String initialBoard, int min, int max) {
		List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
//		HashSet<String> deadBoards = new HashSet<String>();
		int count = 0;
		Board b = new Board(initialBoard);
		//rotate 1-6  in the top positions
//		for (int aa = min; aa <= min; aa++) {
//			System.out.println("going for " + aa);
//			b.overwriteTile(0, 0, aa);
//			for (int ab = min; ab <= min; ab++) {
//				System.out.println("going for " + aa + "," + ab);
//				b.overwriteTile(0, 1, ab);
//				for (int ac = min; ac <= max; ac++) {
//					System.out.println("going for " + ac);
//					b.overwriteTile(0, 2, ac);
					for (int ad = min; ad <= max; ad++) {
						b.overwriteTile(0, 3, ad);
						for (int ba = min; ba <= max; ba++) {
							b.overwriteTile(1, 0, ba);
							for (int bb = min; bb <= max; bb++) {
								b.overwriteTile(1, 1, bb);
								for (int bc = min; bc <= max; bc++) {
									b.overwriteTile(1, 2, bc);
									for (int bd = min; bd <= max; bd++) {
										b.overwriteTile(1, 3, bd);
										for (int ca = min; ca <= max; ca++) {
											b.overwriteTile(2, 0, ca);
											for (int cb = min; cb <= max; cb++) {
												b.overwriteTile(2, 1, cb);
												for (int cc = min; cc <= max; cc++) {
													b.overwriteTile(2, 2, cc);
													for (int cd = min; cd <= max; cd++) {
														b.overwriteTile(2, 3, cd);													
								//check...things
														if (BoardUtil.isDeadBoard(b)) {
															writes.add(new InsertOneModel<Document>(new Document("board", b.serialize())));
//															deadBoards.add(b.serialize());
														}else count++;
														
													}
												}
											}
										}
									}
								}
							}
						}
					}
//				}
//			}
//		}
		System.out.println(new Date() + " live count " + count + ", dead count " + writes.size());
//		return deadBoards;
		return writes;
	}
	
//	public static HashSet<String> ones(String initialBoard, int min, int max) {
	public static List<WriteModel<Document>> ones(String initialBoard, int min, int max) {
		List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
		HashSet<String> deadBoards = new HashSet<String>();
		int count = 0;
		Board b = new Board(initialBoard);
		//rotate 1-6  in the top positions
//		for (int aa = min; aa <= min; aa++) {
//			System.out.println("going for " + aa);
//			b.overwriteTile(0, 0, aa);
//			for (int ab = min; ab <= min; ab++) {
//				System.out.println("going for " + aa + "," + ab);
//				b.overwriteTile(0, 1, ab);
//				for (int ac = min; ac <= max; ac++) {
//					System.out.println("going for " + ac);
//					b.overwriteTile(0, 2, ac);
					for (int ad = min; ad <= max; ad++) {
						b.overwriteTile(0, 3, ad);
						for (int ba = min; ba <= max; ba++) {
							System.out.println(b.serialize());
							b.overwriteTile(1, 0, ba);
							for (int bb = min; bb <= max; bb++) {
								b.overwriteTile(1, 1, bb);
								for (int bc = min; bc <= max; bc++) {
									b.overwriteTile(1, 2, bc);
									for (int bd = min; bd <= max; bd++) {
										b.overwriteTile(1, 3, bd);
										for (int ca = min; ca <= max; ca++) {
											b.overwriteTile(2, 0, ca);
											for (int cb = min; cb <= max; cb++) {
												b.overwriteTile(2, 1, cb);
												for (int cc = min; cc <= max; cc++) {
													b.overwriteTile(2, 2, cc);
													for (int cd = min; cd <= max; cd++) {
														b.overwriteTile(2, 3, cd);													
								//check...things
														if (BoardUtil.countColsMove(b) == 0 && BoardUtil.countRowsMove(b) == 1) {
															Document d = new Document();
															double value = ZeroMoves.zeroOneRowBoardScore(b,2);
															d.put("odds", value);
															d.put("board", b.serialize());
															writes.add(new InsertOneModel<Document>(d));
//															deadBoards.add(b.serialize());
														}
														else count++;
														
													}
												}
											}
										}
									}
								}
							}
						}
					}
//				}
//			}
//		}
		System.out.println(new Date() + " live count " + count + ", dead count " + deadBoards.size());
//		return deadBoards;
		return writes;
	}
}
