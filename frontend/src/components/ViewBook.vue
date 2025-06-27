<template>
    <v-card outlined style="border-radius: 0; margin-top: 10px; padding-bottom: 10px; background-color: #F4F5FA;">
        <v-card-title>
            도서 조회
        </v-card-title>
    
        <v-card-text style="margin-top:-10px;">
            <v-row no-gutters style="margin: 15px 0px -10px -15px;">
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="Id" v-model="value.parameters.id" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <String class="attributes-list" label="도서명" v-model="value.parameters.bookName" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <String class="attributes-list" label="카테고리" v-model="value.parameters.category" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Boolean class="attributes-list" label="베스트셀러여부" v-model="value.parameters.isBestSeller" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <String class="attributes-list" label="작가명" v-model="value.parameters.authorName" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="조회수" v-model="value.parameters.조회수" :editMode="editMode"/>
                </v-col>
                <v-col style="max-width:140px;">
                    <Number class="attributes-list" label="Point" v-model="value.parameters.point" :editMode="editMode"/>
                </v-col>
                <v-col>
                    <v-btn class="gs-query-search-btn contrast-primary-text"
                        @click="search"
                        small
                        style="margin-left: 35px; margin-top: 30px;"
                        color="primary"
                    >
                        <v-icon small>mdi-magnify</v-icon>검색
                    </v-btn>
                </v-col>
            </v-row>
        </v-card-text>
    </v-card>
</template>

<script>
    export default {
        name: 'ViewBook',
        components:{
        },
        props: {},
        data: () => ({
            editMode: true,
            value: {
                apiPath: 'books/search/viewBook',
                parameters: {}
            },
        }),
        created() {
        },
        watch: {
        },
        methods: {
            search() {
                let search = null;
                search = this.value;
                this.$emit('search', search);
            },
            change() {
                this.$emit("update:modelValue", this.value);
            },
        }
    }
</script>
<style>
.attributes-list{
    margin-left: 15px;
}
</style>

