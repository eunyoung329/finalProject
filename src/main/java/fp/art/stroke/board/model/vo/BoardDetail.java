package fp.art.stroke.board.model.vo;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardDetail {
	private int boardId;
	private String boardTitle;
	private String boardContent;
	private String createDate;
	private String updateDate;
	private int readCount;
	private String memberNickname;
	private String profileImage;
	private int memberId;
	
	private List<BoardImage> imageList;
	
	private int boardCode;
}
