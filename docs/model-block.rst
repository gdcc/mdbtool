Metadata Block Model
====================

.. list-table:: Metadata Block Properties
  * - Property
    - Purpose
    - Allowed Values and Restrictions
  * - name
    - A user-definable string used to identify a metadata block.
    - 1. No spaces or punctuation, except underscore.
      2. By convention, should start with a letter, and use lower camel case [3]_
      3. Must not collide with a field of the same name in the same or any other #datasetField definition including metadata blocks defined elsewhere. [4]_
  * - dataverseAlias
    - If specified, this metadata block will be available only to the Dataverse collection designated here by its alias and to children of that Dataverse collection.
    - Free text. For an example, see custom_hbgdki.tsv
  * - displayName
    - Acts as a brief label for display related to this metadata block.
    - Should be relatively brief.
      The limit is 256 character, but very long names might cause display problems.
  * - displayFacet
    - Label displayed in the search area when this #metadataBlock is configured as a search facet for a collection. See :ref:`the API <metadata-block-facet-api>`.
    - Should be brief. Long names will cause display problems in the search area.
  * - blockURI
    - Associates the properties in a block with an external URI.
      Properties will be assigned the global identifier blockURI<name> in the OAI_ORE metadata and archival Bags
    - The citation #metadataBlock has the blockURI https://dataverse.org/schema/citation/ which assigns a default global URI to terms such as https://dataverse.org/schema/citation/subtitle
