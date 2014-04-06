package com.example.page;

public class PageRequest {
	public static final int DEFAULT_SIZE = 10;
	private final int page;
	private final int size;

	public PageRequest(int page, int size) {
		super();
		if (page <= 0) {
			page = 1;
		}
		this.page = page;
		if (size < 0) {
			size = DEFAULT_SIZE;
		}
		this.size = size;
	}

	public int getPageSize() {
		return size;
	}

	int getPageNumber() {
		return page;
	}

	public int getOffset() {
		return page * size;
	}
}
