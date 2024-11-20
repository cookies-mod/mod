package codes.cookies.mod.data.cookiesdata;

import codes.cookies.mod.utils.json.JsonSerializable;
import lombok.SneakyThrows;

public interface CookiesModData extends JsonSerializable {
	String getFileLocation();

	@SneakyThrows
	default void save()
	{
		CookieDataManager.save(this);
	}
}
