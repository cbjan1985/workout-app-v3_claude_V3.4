package APP_PACKAGE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;

/**
 * V3 folder access.
 * - listGifs: lists media in a folder, found case-insensitively by name (internal storage root, Download,
 *   Documents, Pictures, Movies, DCIM) or given as an absolute path. Sub-folders are included (2 levels).
 * - pickFolder: opens Android's folder picker (Storage Access Framework), keeps read permission and returns
 *   the matching file-system path so large files are read in place and never copied.
 * Reading files by path needs "Allow access to manage all files" (requestAccess opens that screen).
 */
@CapacitorPlugin(name = "GifFolder")
public class GifFolderPlugin extends Plugin {

  private static final int MAX_FILES = 5000;

  private boolean hasAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return Environment.isExternalStorageManager();
    }
    if (Build.VERSION.SDK_INT >= 23) {
      return getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  private boolean isMedia(String name) {
    String n = name.toLowerCase();
    return n.endsWith(".gif") || n.endsWith(".webp") || n.endsWith(".png") || n.endsWith(".jpg")
      || n.endsWith(".jpeg") || n.endsWith(".mp4") || n.endsWith(".webm") || n.endsWith(".mkv")
      || n.endsWith(".3gp") || n.endsWith(".m4v") || n.endsWith(".mov");
  }

  private File findDir(String name) {
    File root = Environment.getExternalStorageDirectory();
    String[] parents = { "", "Download", "Documents", "Pictures", "Movies", "DCIM" };
    for (String p : parents) {
      File base = p.isEmpty() ? root : new File(root, p);
      File[] kids = base.listFiles();
      if (kids == null) continue;
      for (File f : kids) {
        if (f.isDirectory() && f.getName().equalsIgnoreCase(name)) return f;
      }
    }
    return null;
  }

  private void collect(File dir, int depth, JSArray out) {
    File[] list = dir.listFiles();
    if (list == null) return;
    for (File f : list) {
      if (out.length() >= MAX_FILES) return;
      if (f.isFile() && isMedia(f.getName())) {
        JSObject o = new JSObject();
        o.put("name", f.getName());
        o.put("path", f.getAbsolutePath());
        out.put(o);
      } else if (f.isDirectory() && depth > 0) {
        collect(f, depth - 1, out);
      }
    }
  }

  @PluginMethod
  public void requestAccess(PluginCall call) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Intent intent = new Intent(
          Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
          Uri.parse("package:" + getContext().getPackageName())
        );
        getActivity().startActivity(intent);
      } else if (Build.VERSION.SDK_INT >= 23) {
        getActivity().requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 4242);
      }
      call.resolve();
    } catch (Exception e) {
      call.reject(String.valueOf(e.getMessage()));
    }
  }

  @PluginMethod
  public void listGifs(PluginCall call) {
    String folder = call.getString("folder", "workout_gif");
    String path = call.getString("path", "");
    JSObject result = new JSObject();
    JSArray files = new JSArray();
    boolean granted = hasAccess();
    File dir = null;
    try {
      if (path != null && !path.isEmpty()) {
        dir = new File(path);
      } else if (folder != null && !folder.isEmpty()) {
        dir = findDir(folder);
      }
      if (granted && dir != null && dir.isDirectory()) {
        collect(dir, 2, files);
      }
    } catch (Exception e) {
      result.put("error", String.valueOf(e.getMessage()));
    }
    result.put("files", files);
    result.put("exists", dir != null && dir.isDirectory());
    result.put("granted", granted);
    result.put("path", dir == null ? "" : dir.getAbsolutePath());
    call.resolve(result);
  }

  @PluginMethod
  public void pickFolder(PluginCall call) {
    try {
      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
      intent.addFlags(
        Intent.FLAG_GRANT_READ_URI_PERMISSION
          | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
          | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
      );
      startActivityForResult(call, intent, "pickFolderResult");
    } catch (Exception e) {
      call.reject(String.valueOf(e.getMessage()));
    }
  }

  @ActivityCallback
  private void pickFolderResult(PluginCall call, ActivityResult result) {
    if (call == null) return;
    if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null || result.getData().getData() == null) {
      call.reject("cancelled");
      return;
    }
    try {
      Uri uri = result.getData().getData();
      try {
        getContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
      } catch (Exception ignored) {
        // permission could not be kept; the file-system path is still returned
      }
      String docId = DocumentsContract.getTreeDocumentId(uri);
      int c = docId.indexOf(':');
      String vol = c >= 0 ? docId.substring(0, c) : docId;
      String rel = c >= 0 ? docId.substring(c + 1) : "";
      File base = "primary".equals(vol) ? Environment.getExternalStorageDirectory() : new File("/storage/" + vol);
      File dir = rel.isEmpty() ? base : new File(base, rel);
      JSObject r = new JSObject();
      r.put("path", dir.getAbsolutePath());
      r.put("exists", dir.isDirectory());
      r.put("uri", uri.toString());
      r.put("granted", hasAccess());
      call.resolve(r);
    } catch (Exception e) {
      call.reject(String.valueOf(e.getMessage()));
    }
  }
}
