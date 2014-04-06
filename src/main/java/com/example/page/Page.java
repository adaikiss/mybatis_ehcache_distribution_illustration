package com.example.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 分页辅助类
 * 
 * @author hlw
 * 
 * @param <T>
 *            Page中记录的类型
 */
public class Page<T> {
	private List<T> content = new ArrayList<T>();
	private final PageRequest pageable;
	private final long total;

	public Page(List<T> content, PageRequest pageable, long total) {

		if (null == content) {
			throw new IllegalArgumentException("Content must not be null!");
		}

		this.content = content;
		this.total = total;
		this.pageable = pageable;
	}

	public Page(List<T> content) {

		this(content, null, (null == content) ? 0 : content.size());
	}

	public int getNumber() {

		return pageable == null ? 0 : pageable.getPageNumber();
	}

	public int getSize() {

		return pageable == null ? 0 : pageable.getPageSize();
	}

	public int getTotalPages() {

		return getSize() == 0 ? 0 : (int) Math.ceil((double) total
				/ (double) getSize());
	}

	public int getNumberOfElements() {

		return content.size();
	}

	public long getTotalElements() {

		return total;
	}

	public boolean hasPreviousPage() {

		return getNumber() > 0;
	}

	public boolean isFirstPage() {

		return !hasPreviousPage();
	}

	public boolean hasNextPage() {

		return ((getNumber() + 1) * getSize()) < total;
	}

	public boolean isLastPage() {

		return !hasNextPage();
	}

	public Iterator<T> iterator() {

		return content.iterator();
	}

	public List<T> getContent() {

		return Collections.unmodifiableList(content);
	}

	public boolean hasContent() {

		return !content.isEmpty();
	}

	@Override
	public String toString() {

		String contentType = "UNKNOWN";

		if (content.size() > 0) {
			contentType = content.get(0).getClass().getName();
		}

		return String.format("Page %s of %d containing %s instances",
				getNumber(), getTotalPages(), contentType);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Page<?>)) {
			return false;
		}

		Page<?> that = (Page<?>) obj;

		boolean totalEqual = this.total == that.total;
		boolean contentEqual = this.content.equals(that.content);
		boolean pageableEqual = this.pageable == null ? that.pageable == null
				: this.pageable.equals(that.pageable);

		return totalEqual && contentEqual && pageableEqual;
	}

	@Override
	public int hashCode() {

		int result = 17;

		result = 31 * result + (int) (total ^ total >>> 32);
		result = 31 * result + (pageable == null ? 0 : pageable.hashCode());
		result = 31 * result + content.hashCode();

		return result;
	}
}
