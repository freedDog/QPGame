package com.game.room.base;

import com.game.entity.shared.RoomPlayerInfo;
import com.game.proto.rp.room.QuickJoinRoomMsg;
import com.game.proto.rp.video.VideoMsg;

public abstract class IGameRoom {
	public void init() {
	}

	public void quickJoinRoom(final RoomPlayerInfo playerInfo, final QuickJoinRoomMsg reqMsg, final long[] filterIds) {
	}

	public Class<?> getGameClass() {
		return null;
	}

	public String createVideoText(VideoMsg vmsg) {
		return null;
	}
}