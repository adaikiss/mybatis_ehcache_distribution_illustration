/**
 * 
 */
package com.example.mapper;

import java.util.List;

import com.example.model.Foo;
import com.example.model.FooExample;

/**
 * @author hlw
 * 
 */
public interface FooMapper {
	List<Foo> selectByExample(FooExample example);
}
