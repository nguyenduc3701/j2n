/**
 * cleanObject - Filters out empty strings, nulls, and undefined values from an object.
 * Also handles specific conversions for common fields like 'role_id' and 'id'.
 */
export const cleanObject = (obj: any) => {
  const cleaned: any = {};
  Object.keys(obj).forEach((key) => {
    const value = obj[key];
    if (value !== "" && value !== null && value !== undefined) {
      if (key === "role_id" || key === "id") {
        cleaned[key] = Number(value);
      } else {
        cleaned[key] = value;
      }
    }
  });
  return cleaned;
};
