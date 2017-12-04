package android.app.printerapp.model;

import java.util.List;

/**
 * Created by SAMSUNG on 2017-12-04.
 */

public class MaterialList {
        private List<Material> materialsApi;

        public List<Material> getMaterials() {
            return materialsApi;
        }

        public void setDetails(List<Material> materials) {
            this.materialsApi = materials;
        }
}
