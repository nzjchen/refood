<template>
    <div>
        <vs-row style="display: flex; justify-content: flex-end">
            <vs-button icon="add_box" id="add-button" @click="openImageUpload" label="Upload Images">Upload Images</vs-button>
        </vs-row>
        <!-- Image Card -->
        <vs-card v-for="image in images" :key="image.id" id="images-list" style="padding: 0px">
            <ReImage v-on:delete="deleteImage(image.id)" v-on:updatePrimary="updatePrimaryImage(image.id)" :imagePath="image.fileName" :isUser="true" :primaryImagePath="primaryImagePath" class="title-image"></ReImage>
        </vs-card>
        <input v-if="getLoggedInUser() == user.id" type="file" id="fileUpload" ref="fileUpload" style="display: none;" multiple @change="uploadImage($event)"/>
    </div>
</template>
<script>

import ReImage from "./ReImage";
import api from "../Api";
import { bus } from "../main";
import {store} from "../store";

const UserImages = {
    name: "UserImages",
    components: { ReImage },
    props: {
        images: {
            type: Array,
            default: null,
        },
        primaryImagePath: {
            type: String,
            default: null,
        },
        user: {
            type: Object,
            default: null
        },
    },

    methods: {
        /**
         * Trigger the file upload box to appear.
         * Used for when the actions dropdown add image action or add image button is clicked.
         */
        openImageUpload: function() {
            this.$refs.fileUpload.click();
        },

        getLoggedInUser: function() {
            return store.loggedInUserId;
        },

        /**
         * Upload business image when image is uploaded on web page
         * @param e Event object which contains file uploaded
         */
        uploadImage: async function(e) {
            //Setup FormData object to send in request
            this.$vs.loading(); //Loading spinning circle while image is uploading (can remove if not wanted)
            for (let image of e.target.files) {
                const fd = new FormData();
                fd.append('filename', image, image.name);
                await api.postUserImage(this.user.id, fd) //!! TODO !! Change to user image endpoint
                    .then(() => { //On success
                        this.$emit("getUser");
                        this.$vs.notify({title:`Image for ${this.user.firstName} was uploaded`, color:'success'});
                        bus.$emit('updatedUserPicture', 'pic updated')
                    })
                    .catch((error) => { //On fail
                        if (error.response.status === 400) {
                            this.$vs.notify({title:`Failed To Upload Image`, text: "The supplied file is not a valid image.", color:'danger'});
                        } else if (error.response.status === 500) {
                            this.$vs.notify({title:`Failed To Upload Image`, text: 'There was a problem with the server.', color:'danger'});
                        } else if (error.response.status === 413) {
                            this.$vs.notify({title:`Failed To Upload Image`, text: 'The image is too large.', color:'danger'});
                        }
                    })
                    .finally(() => {
                        this.$vs.loading.close();
                    });
            }
        },

        /**
         * Call api endpoint to update the primary image for the business.
         */
        updatePrimaryImage: function(imageId) {
            api.changeUserPrimaryImage(this.user.id, imageId)
                .then(async () => {
                    this.$emit("getUser");
                    this.$vs.notify({title:`Successfully Updated Primary Image`, color:'success'})
                    bus.$emit('updatedUserPicture', 'pic updated')
                    location.reload();
                })
                .catch((error) => {
                    if (error.status !== 500) {
                        this.$vs.notify({title:`Failed To Update Primary Image`, text: 'Error'+error, color:'danger'});
                    } else if (error.status === 500) {
                        this.$vs.notify({title:`Failed To Update Primary Image`, text: 'There was a problem with the server.', color:'danger'});
                    }
                });
        },

        /**
         * Call delete image endpoint for business.
         * @param imageId
         */
        deleteImage(imageId) {
            api.deleteUserImage(this.user.id, imageId) // !! TODO !! Change to user endpoint
                .then(() => {
                    bus.$emit('updatedUserPicture', 'pic updated')
                    this.$emit('getUser');
                    this.$vs.notify({title:`Image Has Been Successfully Removed`, color:'success'})
                })
                .catch((error) => {
                    if (error.status !== 500) {
                        this.$vs.notify({title:`Failed To Remove Image`, text: 'Error'+error, color:'danger'});
                    } else if (error.status === 500) {
                        this.$vs.notify({title:`Failed To Remove Image`, text: 'There was a problem with the server.', color:'danger'});
                    }
                });
        }
    },
}

export default UserImages;
</script>
<style scoped>
    #images-list {
        margin-left: auto;
        margin-right: auto;
        display: flex;
        width: 430px;
        height: 320px;
        justify-content: center;
        flex-wrap: wrap;
        margin-top: 15px;
    }
    #add-button {
        margin-right: 10%;
        margin-top: 15px;
    }
    .image-card {
        margin: 100px;
        min-width: 200px;
        padding: 4px;
    }
    .title-image {
        height: 320px;
        width: 430px;
        object-fit: cover;
        display: flex;
        grid-column: 1;
        grid-row: 1 / 3;
        margin-left: auto;
        justify-content: center;
        margin-bottom: 10px;
    }
    .edit-button {
        position: relative;
        right: 10px;
        top: 10px;
    }
</style>


