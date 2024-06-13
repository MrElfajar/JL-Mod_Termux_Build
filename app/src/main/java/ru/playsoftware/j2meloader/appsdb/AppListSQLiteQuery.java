/*
 * Copyright 2023-2024 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.playsoftware.j2meloader.appsdb;

import static ru.playsoftware.j2meloader.util.Constants.PREF_APP_SORT;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.sqlite.db.SupportSQLiteProgram;
import androidx.sqlite.db.SupportSQLiteQuery;

import ru.playsoftware.j2meloader.EmulatorApplication;
import ru.playsoftware.j2meloader.R;

class AppListSQLiteQuery implements SupportSQLiteQuery {
	private static final String SELECT = "SELECT * FROM `apps` " +
			"WHERE `title` LIKE '%' || ? || '%' OR `author` LIKE '%' || ? || '%' " +
			"ORDER BY ";

	private final String[] orderTerms;
	private int sortVariant;
	private String filter = "";
	private String sql;

	AppListSQLiteQuery() {
		Context context = EmulatorApplication.getInstance();
		orderTerms = context.getResources().getStringArray(R.array.pref_app_sort_values);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			sortVariant = preferences.getInt(PREF_APP_SORT, 0);
		} catch (Exception e) {
			sortVariant = preferences.getString(PREF_APP_SORT, "name").equals("name") ? 0 : 1;
			preferences.edit().putInt(PREF_APP_SORT, sortVariant).apply();
		}
		updateQuery();
	}

	@NonNull
	@Override
	public String getSql() {
		return sql;
	}

	@Override
	public void bindTo(@NonNull SupportSQLiteProgram statement) {
		statement.bindString(1, filter);
		statement.bindString(2, filter);
	}

	@Override
	public int getArgCount() {
		return 2;
	}

	boolean setSort(int sort) {
		if (sort == sortVariant) {
			return false;
		}
		sortVariant = sort;
		updateQuery();
		return true;
	}

	boolean setFilter(String filter) {
		if (this.filter.equals(filter)) {
			return false;
		}
		this.filter = filter;
		updateQuery();
		return true;
	}

	private void updateQuery() {
		String order = sortVariant < 0 ? " DESC" : " ASC";
		sql = SELECT + String.format(orderTerms[sortVariant & 0x7FFFFFFF], order);
	}

	String getFilter() {
		return filter;
	}
}
