# Adds the permissions this app needs to the generated Android manifest.
p = "android/app/src/main/AndroidManifest.xml"
s = open(p, encoding="utf-8").read()
perms = (
    '    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />\n'
    '    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />\n'
    '    <uses-permission android:name="android.permission.VIBRATE" />\n'
)
if "MANAGE_EXTERNAL_STORAGE" not in s:
    assert "<application" in s
    s = s.replace("<application", perms + "    <application", 1)
open(p, "w", encoding="utf-8").write(s)
print("Manifest patched")
